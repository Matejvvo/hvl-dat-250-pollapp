package no.hvl.dat250.pollapp.web.req;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoteCreateRequest(
        @NotNull UUID voterId,
        @NotNull UUID optionId
) {}
