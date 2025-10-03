package no.jathvl.dat250experiment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.jathvl.dat250experiment.dto.CreatePollRequest;
import no.jathvl.dat250experiment.model.Poll;
import no.jathvl.dat250experiment.repository.PollManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;

@RestController
@CrossOrigin
public class PollController {
    private final PollManager pollManager;

    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping("/poll/create")
    public Poll createPoll(@RequestBody CreatePollRequest p) {
        var poll = pollManager.createPoll(p.creatorUserId, p.question, Duration.ofHours(p.durationHours), p.options);
        if (poll.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }

        return poll.get();
    }

    @GetMapping("/poll/{id}")
    public Optional<Poll> getPoll(@PathVariable Integer id) {
        return pollManager.getPoll(id);
    }

    @GetMapping(path = "/poll/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllPolls() {
        var cache = pollManager.getCachedAllPolls();
        if (cache.isPresent()) {
            return cache.get();
        }

        try {
            return pollManager.getMapper().writeValueAsString(pollManager.getPolls());
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/poll/{id}")
    public void deletePoll(@PathVariable Integer id) {
        var poll = pollManager.getPoll(id);
        if (poll.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }

        pollManager.deletePoll(poll.get());
    }
}
