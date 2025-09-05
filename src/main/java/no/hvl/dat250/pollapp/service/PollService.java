package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.model.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PollService {
    Poll create(String question, int maxVotesPerUser, boolean isPrivate, UUID creatorId,
                Instant publishedAt, Instant validUntil, List<String> options);

    List<Poll> list();

    Poll get(UUID pollId);

    Poll update(UUID pollId, String question, int maxVotesPerUser, boolean isPrivate, Instant validUntil);

    void delete(UUID pollId);

    VoteOption addVoteOption(UUID pollId, String caption);

    List<VoteOption> listVoteOptions(UUID pollId);

    void removeVoteOption(UUID pollId, UUID optionId);

    User addAllowedVoter(UUID pollId, UUID userId);

    List<User> listAllowedVoters(UUID pollId);

    void removeAllowedVoter(UUID pollId, UUID userId);

    Vote castVote(UUID voterId, UUID pollId, UUID optionId);

    List<Vote> listPollVotes(UUID pollId);

    String getAggregatedResults(UUID pollId);
}
