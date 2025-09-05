package no.jathvl.dat250experiment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class VoteOption {
    public int id;

    public String caption;
    public int presentationOrder;

    @JsonBackReference
    public Poll poll;

    @JsonManagedReference
    public List<Vote> votes = new ArrayList<>();

    public VoteOption(int id, String caption, int presentationOrder, Poll poll) {
        this.id = id;
        this.caption = caption;
        this.presentationOrder = presentationOrder;
        this.poll = poll;
    }
}
