package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.domain.*;
import no.hvl.dat250.pollapp.service.UserService;
import no.hvl.dat250.pollapp.web.dto.UserCreateRequest;
import no.hvl.dat250.pollapp.web.dto.UserUpdateRequest;
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
    public User create(@RequestBody UserCreateRequest req) {
        return userService.create(req.username(), req.email(), "");
    }

    @GetMapping
    public List<User> list() {
        if (userService.list().isEmpty()) return List.of();
        return userService.list().stream().toList();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable UUID id) {
        return userService.get(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody UserUpdateRequest req) {
        return userService.update(id, req.username(), req.email());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/polls")
    public List<Poll> getPolls(@PathVariable UUID id) {
        if (userService.listPolls(id).isEmpty()) return List.of();
        return userService.listPolls(id);
    }

    @GetMapping("/{id}/votes")
    public List<Vote> getVotes(@PathVariable UUID id) {
        if (userService.listVotes(id).isEmpty()) return List.of();
        return userService.listVotes(id);
    }
}
