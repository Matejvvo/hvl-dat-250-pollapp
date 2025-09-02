package no.hvl.dat250.pollapp.dto;

import no.hvl.dat250.pollapp.model.Poll;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record PollDTO(
        UUID id,
        String question,
        Instant publishedAt,
        Instant validUntil,
        int maxVotesPerUser,
        boolean isPrivate,
        UserDTO creator,
        Set<UserDTO> allowedVoters,
        List<VoteOptionDTO> options
) {
    public static PollDTO from(Poll poll) {
        return from(poll, true);
    }

    // toggle deep/shallow mapping
    static PollDTO from(Poll poll, boolean deep) {
        if (poll == null) return null;
        return new PollDTO(
                poll.getId(),
                poll.getQuestion(),
                poll.getPublishedAt(),
                poll.getValidUntil(),
                poll.getMaxVotesPerUser(),
                poll.getIsPrivate(),
                deep ? UserDTO.from(poll.getCreator()) : null,
                deep ? poll.getAllowedVoters().stream()
                        .map(UserDTO::from)
                        .collect(Collectors.toSet()) : Set.of(),
                poll.getOptions().stream()
                        .map(o -> VoteOptionDTO.from(o, false))
                        .collect(Collectors.toList())
        );
    }
}
