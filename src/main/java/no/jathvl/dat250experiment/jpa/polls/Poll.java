package no.jathvl.dat250experiment.jpa.polls;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question;

    @ManyToOne
    private User createdBy;

    @OneToMany(cascade = CascadeType.ALL)
    private List<VoteOption> options = new ArrayList<>();

    public Poll() {}

    protected Poll(String question, User createdBy) {
        this.question = question;
        this.createdBy = createdBy;
    }

    public VoteOption addVoteOption(String content) {
        var vo = new VoteOption(content, options.size(), this);
        options.add(vo);
        return vo;
    }
}
