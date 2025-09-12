package no.jathvl.dat250experiment.controller;

import no.jathvl.dat250experiment.dto.CreatePollRequest;
import no.jathvl.dat250experiment.model.Poll;
import no.jathvl.dat250experiment.repository.PollManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Collection;
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

    @GetMapping("/poll/all")
    public Collection<Poll> getAllPolls() {
        return pollManager.getPolls();
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
