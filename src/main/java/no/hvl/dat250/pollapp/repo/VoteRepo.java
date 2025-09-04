package no.hvl.dat250.pollapp.repo;

import no.hvl.dat250.pollapp.model.Vote;

import java.util.List;
import java.util.UUID;

public interface VoteRepo {
    Vote save(Vote vote);

    Vote findById(UUID id);

    List<Vote> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
