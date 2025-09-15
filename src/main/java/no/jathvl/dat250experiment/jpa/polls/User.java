package no.jathvl.dat250experiment.jpa.polls;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Poll> polls = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Poll createPoll(String question) {
        var poll = new Poll(question, this);
        polls.add(poll);
        return poll;
    }

    public Vote voteFor(VoteOption opt) {
        var vote = new Vote(this, opt);
        votes.add(vote);
        opt.addVote(vote);
        return vote;
    }
}
