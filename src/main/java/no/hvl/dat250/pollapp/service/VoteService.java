package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.dto.VoteDTO;

import java.util.List;
import java.util.UUID;

public interface VoteService {
    VoteDTO castVote(UUID voterId, UUID pollId, UUID optionId);

    List<VoteDTO> listPollVotes(UUID pollId);

    VoteDTO get(UUID voteId);

    VoteDTO update(UUID userId, UUID voteId, UUID optionId);

    void delete(UUID userId, UUID voteId);
}
