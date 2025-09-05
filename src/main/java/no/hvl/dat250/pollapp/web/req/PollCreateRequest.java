package no.hvl.dat250.pollapp.web.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PollCreateRequest(
        @NotBlank String question,
        @Positive int maxVotesPerUser,
        boolean isPrivate,
        @NotNull UUID creatorId,
        @NotNull Instant publishedAt,
        @NotNull Instant validUntil,
        @NotNull List<String> options
) {}
