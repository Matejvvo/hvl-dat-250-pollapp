package no.hvl.dat250.pollapp.repository.interfaces;

import no.hvl.dat250.pollapp.domain.Vote;

import java.util.List;
import java.util.UUID;

public interface VoteRepo {
    Vote save(Vote vote);

    Vote findById(UUID id);

    List<Vote> findAll();

    boolean existsById(UUID id);

    boolean empty();

    void deleteById(UUID id);
}
