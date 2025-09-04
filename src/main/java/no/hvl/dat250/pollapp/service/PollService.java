package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.dto.PollDTO;
import no.hvl.dat250.pollapp.dto.UserDTO;
import no.hvl.dat250.pollapp.dto.VoteOptionDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PollService {
    PollDTO create(String q, int maxVotes, boolean priv, UUID userId, Instant publishedAt, Instant validUntil);

    List<PollDTO> list();

    PollDTO get(UUID pollId);

    PollDTO update(UUID pollId, String q, int maxVotes, boolean priv, Instant validUntil);

    void delete(UUID pollId);

    VoteOptionDTO addVoteOption(UUID pollId, String caption);

    List<VoteOptionDTO> listVoteOptions(UUID pollId);

    // VoteOptionDTO updateVoteOption(UUID pollId, UUID optionId, VoteOptionDTO option);

    void removeVoteOption(UUID pollId, UUID optionId);

    UserDTO addAllowedVoter(UUID pollId, UUID userId);

    List<UserDTO> listAllowedVoters(UUID pollId);

    UserDTO removeAllowedVoter(UUID pollId, UUID userId);

    String getAggregatedResults(UUID pollId);
}
