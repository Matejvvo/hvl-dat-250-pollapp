package no.hvl.dat250.pollapp.repo;

import no.hvl.dat250.pollapp.model.Poll;

import java.util.List;
import java.util.UUID;

public interface PollRepo {
    Poll save(Poll poll);

    Poll findById(UUID id);

    List<Poll> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
