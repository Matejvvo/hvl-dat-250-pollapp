package no.hvl.dat250.pollapp.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public record PollUpdateRequest(
        String question,
        @PositiveOrZero Integer maxVotesPerUser, // only apply if greater than current
        Boolean isPrivate,
        Instant validUntil
) {}
