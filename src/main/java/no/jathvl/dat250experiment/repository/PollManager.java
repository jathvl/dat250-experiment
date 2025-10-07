package no.jathvl.dat250experiment.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.valkey.JedisPubSub;
import io.valkey.UnifiedJedis;
import no.jathvl.dat250experiment.model.Poll;
import no.jathvl.dat250experiment.model.User;
import no.jathvl.dat250experiment.model.Vote;
import no.jathvl.dat250experiment.model.VoteOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Repository
public class PollManager {
    private final HashMap<Integer, User> users = new HashMap<>();

    private int maxUserId = 0;
    private int maxPollId = 0;
    private int maxVoteId = 0;
    private int maxVoteOptionId = 0;

    private final UnifiedJedis jedis;
    private final String cacheKey = "all-polls-cache";
    private final ObjectMapper mapper = new ObjectMapper();

    private final Set<String> subscribedTopics = new ConcurrentSkipListSet<>();
    private Thread subscriberThread;
    private final MySubscriber subscriber = new MySubscriber();

    public PollManager(@Value("${jathvl.valkey_conn_str}") String ValkeyConnectionString) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());

        jedis = new UnifiedJedis(ValkeyConnectionString);
        startSubscribing();
        clearCache();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    private synchronized void clearCache() {
        this.jedis.del(cacheKey);
    }

    private synchronized void setCache(Collection<Poll> polls) {
        try {
            jedis.set(cacheKey, mapper.writeValueAsString(polls));
        } catch (JsonProcessingException e) {
            clearCache();
        }
    }

    public synchronized Optional<String> getCachedAllPolls() {
        var cache = jedis.get(cacheKey);
        if (cache != null) {
            return Optional.of(cache);
        }

        return Optional.empty();
    }

    public void startSubscribing() {
        subscriberThread = new Thread(() -> {
            jedis.subscribe(subscriber, "notifications");
        });
        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    public void addTopic(String topic) {
        subscribedTopics.add(topic);
        subscriber.subscribe(topic);
    }

    public void removeTopic(String topic) {
        subscribedTopics.remove(topic);
        subscriber.unsubscribe(topic);
    }

    public void handleMessage(String channel, String message) {
        int voId = Integer.parseInt(channel.split("voteoption-")[1]);
        int uId = Integer.parseInt(message);
        handleCastVote(uId, voId);
    }

    private class MySubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            handleMessage(channel, message);
        }
    }

    public User createUser(String username, String email) {
        synchronized (users) {
            var u = new User(++maxUserId, username, email);
            users.put(u.id, u);
            return u;
        }
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public Optional<User> getUser(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    public boolean deleteUser(Integer id) {
        synchronized (users) {
            // TODO: remove all polls, votes by the user
            return users.remove(id) != null;
        }
    }

    public Optional<Poll> createPoll(int userId, String question, TemporalAmount duration, List<String> options) {
        synchronized (users) {
            if (!users.containsKey(userId)) {
                return Optional.empty();
            }

            var now = Instant.now();

            var user = users.get(userId);

            var voteOptions = new ArrayList<VoteOption>();
            var p = new Poll(++maxPollId, question, now, now.plus(duration), user, voteOptions);
            for (int i = 0; i < options.size(); i++) {
                var opt = new VoteOption(++maxVoteOptionId, options.get(i), i, p);
                voteOptions.add(opt);
                addTopic(String.format("voteoption-%d", opt.id));
            }

            user.polls.add(p);
            clearCache();
            return Optional.of(p);
        }
    }

    public Collection<Poll> getPolls() {
        var polls = users.values()
                .stream()
                .flatMap(u -> u.polls.stream())
                .distinct()
                .sorted(Comparator.comparingInt(p -> p.id))
                .toList();

        setCache(polls);
        return polls;
    }

    public Optional<Poll> getPoll(Integer id) {
        synchronized (users) {
            // Horribly inefficient
            return getPolls().stream().filter(p -> p.id == id).findFirst();
        }
    }

    public void deletePoll (Poll poll) {
        synchronized (users) {
            for (VoteOption o : poll.options) {
                for (Vote v : o.votes) {
                    v.user.votes.remove(v);
                }
                removeTopic(String.format("voteoption-%d", o.id));
            }
            poll.options.clear();
            poll.creator.polls.remove(poll);

            clearCache();
        }
    }

    public void castVote(int userId, int optionId) {
        jedis.publish(String.format("voteoption-%d", optionId), String.valueOf(userId));
    }

    private boolean handleCastVote(int userId, int optionId) {
        // TODO: resolve potential race conditions related to the VoteOption being used by reference
        synchronized (users) {
            if (!users.containsKey(userId)) {
                return false;
            }

            var opt = getPolls()
                    .stream()
                    .map(p -> p.options
                            .stream()
                            .filter(o -> o.id == optionId)
                            .findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
            if (opt.isEmpty()) {
                return false;
            }
            var option = opt.get();

            var now = Instant.now();
            if (option.poll.validUntil.isBefore(now)) {
                return false;
            }

            var user = users.get(userId);
            var vote = new Vote(++maxVoteId, now, user, option);

            var existingVote = user.votes.stream()
                    .filter(v -> v.option.poll.equals(option.poll))
                    .findAny();
            if (existingVote.isPresent()) {
                var v = existingVote.get();
                user.votes.remove(v);
                v.option.votes.remove(v);
            }

            user.votes.add(vote);
            option.votes.add(vote);

            clearCache();

            return true;
        }
    }
}
