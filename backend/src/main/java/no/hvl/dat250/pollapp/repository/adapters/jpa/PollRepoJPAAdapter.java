package no.hvl.dat250.pollapp.repository.adapters.jpa;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.repository.interfaces.PollRepo;

import no.hvl.dat250.pollapp.repository.jpa.PollRepoJPA;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public class PollRepoJPAAdapter implements PollRepo {
    private final PollRepoJPA repo;

    public PollRepoJPAAdapter(PollRepoJPA repo) {
        this.repo = repo;
    }

    @Override
    public Poll save(Poll poll) {
        return repo.save(poll);
    }

    @Override
    public Poll findById(UUID id) {
        return repo.findById(id.toString()).orElse(null);
    }

    @Override
    public List<Poll> findAll() {
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
