package no.jathvl.dat250experiment.repository;

import no.jathvl.dat250experiment.model.Poll;
import no.jathvl.dat250experiment.model.User;
import no.jathvl.dat250experiment.model.Vote;
import no.jathvl.dat250experiment.model.VoteOption;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;

@Repository
public class PollManager {
    private final HashMap<Integer, User> users = new HashMap<>();

    private int maxUserId = 0;
    private int maxPollId = 0;
    private int maxVoteId = 0;
    private int maxVoteOptionId = 0;

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
                voteOptions.add(new VoteOption(++maxVoteOptionId, options.get(i), i, p));
            }

            user.polls.add(p);
            return Optional.of(p);
        }
    }

    public Collection<Poll> getPolls() {
        return users.values()
                .stream()
                .flatMap(u -> u.polls.stream())
                .distinct()
                .sorted(Comparator.comparingInt(p -> p.id))
                .toList();
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
            }
            poll.options.clear();
            poll.creator.polls.remove(poll);
        }
    }

    public boolean castVote (int userId, int optionId) {
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

            return true;
        }
    }
}
