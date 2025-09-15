package no.jathvl.dat250experiment.jpa.polls;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long voteOptionId;

    private String caption;
    private Integer presentationOrder;

    @ManyToOne
    private Poll poll;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    public VoteOption() {}

    protected VoteOption(String caption, Integer presentationOrder, Poll poll) {
        this.caption = caption;
        this.presentationOrder = presentationOrder;
        this.poll = poll;
    }

    protected void addVote(Vote vote) {
        votes.add(vote);
    }
}
