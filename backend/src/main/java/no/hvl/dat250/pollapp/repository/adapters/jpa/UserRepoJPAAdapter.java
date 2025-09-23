package no.hvl.dat250.pollapp.repository.adapters.jpa;

import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;

import no.hvl.dat250.pollapp.repository.jpa.UserRepoJPA;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public class UserRepoJPAAdapter implements UserRepo {
    private final UserRepoJPA repo;

    public UserRepoJPAAdapter(UserRepoJPA repo) {
        this.repo = repo;
    }

    @Override
    public User save(User poll) {
        return repo.save(poll);
    }

    @Override
    public User findById(UUID id) {
        return repo.findById(id.toString()).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id.toString());
    }

    @Override
    public boolean empty() {
        return repo.findAll().isEmpty();
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id.toString());
    }
}
