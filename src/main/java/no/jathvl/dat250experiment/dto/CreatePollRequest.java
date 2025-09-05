package no.jathvl.dat250experiment.dto;

import java.util.List;

public class CreatePollRequest {
    public Integer creatorUserId;

    public String question;
    public Integer durationHours;
    public List<String> options;
}
