package no.hvl.dat250.pollapp.web.dto;

import jakarta.validation.constraints.NotBlank;

public record OptionCreateRequest(
        @NotBlank String caption
) {}
