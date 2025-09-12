package no.hvl.dat250.pollapp.repository;

import no.hvl.dat250.pollapp.domain.Poll;

import java.util.List;
import java.util.UUID;

public interface PollRepo {
    Poll save(Poll poll);

    Poll findById(UUID id);

    List<Poll> findAll();

    boolean existsById(UUID id);

    boolean empty();

    void deleteById(UUID id);
}
