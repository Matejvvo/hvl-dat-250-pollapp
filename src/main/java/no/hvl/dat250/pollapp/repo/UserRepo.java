package no.hvl.dat250.pollapp.repo;

import no.hvl.dat250.pollapp.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepo {
    User save(User user);

    User findById(UUID id);

    List<User> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
