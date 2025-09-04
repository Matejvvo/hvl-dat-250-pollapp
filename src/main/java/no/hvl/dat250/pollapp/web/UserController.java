package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user.getUsername(), user.getEmail(), "");
    }

    @GetMapping
    public List<User> list() {
        return userService.list().stream().toList();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable UUID id) {
        return userService.get(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User user) {
        return userService.update(id, user.getUsername(), user.getEmail());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/polls")
    public List<Poll> getPolls(@PathVariable UUID id) {
        return userService.listPolls(id);
    }

    @GetMapping("/{id}/votes")
    public List<Vote> getVotes(@PathVariable UUID id) {
        return userService.listVotes(id);
    }
}
