package no.hvl.dat250.pollapp.service.interfaces;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.Vote;

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
