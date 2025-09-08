package no.hvl.dat250.pollapp.web.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record VoteUpdateRequest(
        @NotNull UUID voterId,
        @NotNull UUID optionId
) {}
