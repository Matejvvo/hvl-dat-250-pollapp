package no.hvl.dat250.pollapp.repository.adapters.jpa;

import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.repository.interfaces.VoteRepo;

import no.hvl.dat250.pollapp.repository.jpa.VoteRepoJPA;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public class VoteRepoJPAAdapter implements VoteRepo {
    private final VoteRepoJPA repo;

    public VoteRepoJPAAdapter(VoteRepoJPA repo) {
        this.repo = repo;
    }

    @Override
    public Vote save(Vote poll) {
        return repo.save(poll);
    }

    @Override
    public Vote findById(UUID id) {
        return repo.findById(id.toString()).orElse(null);
    }

    @Override
    public List<Vote> findAll() {
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
