package no.hvl.dat250.pollapp.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public record PollUpdateRequest(
        String question,
        @PositiveOrZero Integer maxVotesPerUser,
        Boolean isPrivate,
        Instant validUntil
) {}
