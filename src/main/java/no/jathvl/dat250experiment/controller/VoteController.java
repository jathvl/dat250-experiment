package no.jathvl.dat250experiment.controller;

import no.jathvl.dat250experiment.dto.SubmitVoteRequest;
import no.jathvl.dat250experiment.repository.PollManager;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class VoteController {
    private final PollManager pollManager;

    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PutMapping("/vote")
    public void submitVote(@RequestBody SubmitVoteRequest s) {
        pollManager.castVote(s.userId, s.voteOptionId);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }
}