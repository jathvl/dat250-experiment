package no.jathvl.dat250experiment.jpa.polls;

import jakarta.persistence.*;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private VoteOption votesOn;

    public Vote() {}

    protected Vote(User user, VoteOption voteOption) {
        this.user = user;
        this.votesOn = voteOption;
    }
}
