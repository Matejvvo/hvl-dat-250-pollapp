package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.domain.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String passwordHash);

    List<User> list();

    User get(UUID userId);

    User update(UUID userId, String username, String email);

    void delete(UUID userId);

    List<Poll> listPolls(UUID userId);

    List<Vote> listVotes(UUID userId);
}
