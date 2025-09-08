package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.domain.*;

import java.util.List;
import java.util.UUID;

public interface VoteService {
    List<Vote> list();

    Vote get(UUID voteId);

    Vote update(UUID voteId, UUID userId, UUID optionId);

    void delete(UUID voteId);
}
