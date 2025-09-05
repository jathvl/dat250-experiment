package no.jathvl.dat250experiment.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Poll {
    public int id;

    public String question;
    public Instant publishedAt;
    public Instant validUntil;

    @JsonManagedReference
    public User creator;

    @JsonManagedReference
    public List<VoteOption> options;

    public Poll(int id, String question, Instant publishedAt, Instant validUntil, User creator, List<VoteOption> options) {
        this.id = id;
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
        this.creator = creator;
        this.options = options;
    }
}
