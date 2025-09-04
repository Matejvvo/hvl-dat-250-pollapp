package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.service.UserService;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;

    public UserServiceImpl(UserRepo userRepo,  PollRepo pollRepo, VoteRepo voteRepo) {
        this.userRepo = userRepo;
        this.pollRepo = pollRepo;
        this.voteRepo = voteRepo;
    }

    @Override
    public User create(String username, String email, String _passwordHash) {
        if (username == null || username.isBlank()) return null;
        if (email == null || email.isBlank()) return null;

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPolls(new HashSet<>());
        user.setVotes(new HashSet<>());

        user = userRepo.save(user);
        return user;
    }

    @Override
    public List<User> list() {
        if (userRepo.empty()) return List.of();
        return userRepo.findAll().stream().toList();
    }

    @Override
    public User get(UUID userId) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;
        return userRepo.findById(userId);
    }

    @Override
    public User update(UUID userId, String username, String email) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;

        User existing = userRepo.findById(userId);
        if (username != null && !username.isBlank()) existing.setUsername(username.trim());
        if (email != null && !email.isBlank()) existing.setEmail(email.trim());

        existing = userRepo.save(existing);
        return existing;
    }

    @Override
    public void delete(UUID userId) {
        if (userId == null) return;
        if (!userRepo.existsById(userId)) return;

        User user = userRepo.findById(userId);
        if (user == null) return;

        user.getPolls().forEach(p -> pollRepo.deleteById(p.getId()));
        user.getVotes().forEach(v -> voteRepo.deleteById(v.getId()));

        userRepo.deleteById(userId);
    }

    @Override
    public List<Poll> listPolls(UUID userId) {
        if (userId == null) return List.of();
        if (!userRepo.existsById(userId)) return List.of();
        return userRepo.findById(userId).getPolls().stream().toList();
    }

    @Override
    public List<Vote> listVotes(UUID userId) {
        if (userId == null) return List.of();
        if (!userRepo.existsById(userId)) return List.of();
        return userRepo.findById(userId).getVotes().stream().toList();
    }
}
