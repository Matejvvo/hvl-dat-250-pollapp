package no.hvl.dat250.pollapp.repository;

import no.hvl.dat250.pollapp.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserRepo {
    User save(User user);

    User findById(UUID id);

    List<User> findAll();

    boolean existsById(UUID id);

    boolean empty();

    void deleteById(UUID id);
}
