package no.jathvl.dat250experiment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Vote {
    public int id;

    public Instant publishedAt;

    @JsonManagedReference
    public User user;

    @JsonBackReference
    public VoteOption option;

    public Vote(int id, Instant publishedAt, User user, VoteOption option) {
        this.id = id;
        this.publishedAt = publishedAt;
        this.user = user;
        this.option = option;
    }
}

