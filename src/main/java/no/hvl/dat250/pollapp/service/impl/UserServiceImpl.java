package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.dto.*;
import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;

    public UserServiceImpl(UserRepo userRepo, PollRepo pollRepo, VoteRepo voteRepo) {
        this.userRepo = userRepo;
        this.pollRepo = pollRepo;
        this.voteRepo = voteRepo;
    }

    @Override
    public UserDTO create(String username, String email) {
        if (username == null || username.isBlank()) return null;
        if (email == null || email.isBlank()) return null;

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPolls(new HashSet<>());
        user.setVotes(new HashSet<>());

        user = userRepo.save(user);
        return UserDTO.from(user);
    }

    @Override
    public List<UserDTO> list() {
        return userRepo.findAll().stream().map(UserDTO::from).toList();
    }

    @Override
    public UserDTO get(UUID userId) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;
        return UserDTO.from(userRepo.findById(userId));
    }

    @Override
    public UserDTO update(UUID userId, String username, String email) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;

        User existing = userRepo.findById(userId);
        if (username != null && !username.isBlank()) existing.setUsername(username.trim());
        if (email != null && !email.isBlank()) existing.setEmail(email.trim());

        existing = userRepo.save(existing);
        return UserDTO.from(existing);
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
    public List<PollDTO> listPolls(UUID userId) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;
        return userRepo.findById(userId).getPolls().stream().map(PollDTO::from).toList();
    }

    @Override
    public List<VoteDTO> listVotes(UUID userId) {
        if (userId == null) return null;
        if (!userRepo.existsById(userId)) return null;
        return userRepo.findById(userId).getVotes().stream().map(VoteDTO::from).toList();
    }
}
