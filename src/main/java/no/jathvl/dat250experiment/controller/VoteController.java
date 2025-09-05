package no.jathvl.dat250experiment.controller;

import no.jathvl.dat250experiment.dto.SubmitVoteRequest;
import no.jathvl.dat250experiment.repository.PollManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class VoteController {
    private final PollManager pollManager;

    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PutMapping("/vote")
    public void submitVote(@RequestBody SubmitVoteRequest s) {
        if(!pollManager.castVote(s.userId, s.voteOptionId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }
}