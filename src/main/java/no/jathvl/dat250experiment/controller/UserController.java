package no.jathvl.dat250experiment.controller;

import no.jathvl.dat250experiment.dto.CreateUserRequest;
import no.jathvl.dat250experiment.model.User;
import no.jathvl.dat250experiment.repository.PollManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class UserController {
    private final PollManager pollManager;

    public UserController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping("/user/create")
    public User createUser(@RequestBody CreateUserRequest u) {
        return pollManager.createUser(u.username, u.email);
    }

    @GetMapping("/user/{id}")
    public Optional<User> getUser(@PathVariable Integer id) {
        return pollManager.getUser(id);
    }

    @GetMapping("/user/all")
    public Collection<User> getUsers() {
        return pollManager.getUsers();
    }
}
