package no.hvl.dat250.pollapp.dto;

import no.hvl.dat250.pollapp.model.Vote;

import java.time.Instant;
import java.util.UUID;

public record VoteDTO(
        UUID id,
        Instant publishedAt,
        UserDTO voter,
        VoteOptionDTO option
) {
    public static VoteDTO from(Vote vote) {
        return from(vote, true);
    }

    static VoteDTO from(Vote vote, boolean deep) {
        if (vote == null) return null;
        return new VoteDTO(
                vote.getId(),
                vote.getPublishedAt(),
                deep ? UserDTO.from(vote.getVoter()) : null,
                deep ? VoteOptionDTO.from(vote.getOption(), false) : null
        );
    }
}
