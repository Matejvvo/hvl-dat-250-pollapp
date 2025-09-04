package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.model.*;

import java.util.List;
import java.util.UUID;

public interface VoteService {
    Vote castVote(UUID voterId, UUID pollId, UUID optionId);

    List<Vote> list();

    List<Vote> listPollVotes(UUID pollId);

    Vote get(UUID voteId);

    Vote update(UUID userId, UUID voteId, UUID optionId);

    void delete(UUID userId, UUID voteId);
}
