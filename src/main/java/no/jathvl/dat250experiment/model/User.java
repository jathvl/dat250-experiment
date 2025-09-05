package no.jathvl.dat250experiment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User {
    public int id;

    public String username;
    public String email;

    @JsonBackReference
    public List<Vote> votes = new ArrayList<>();

    @JsonBackReference
    public List<Poll> polls = new ArrayList<>();

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}

