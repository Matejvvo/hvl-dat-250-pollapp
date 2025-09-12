package no.hvl.dat250.pollapp.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoteCreateRequest(
        @NotNull UUID voterId,
        @NotNull UUID optionId,
        UUID pollId
) {}
