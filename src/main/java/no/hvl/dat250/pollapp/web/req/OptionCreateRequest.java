package no.hvl.dat250.pollapp.web.req;

import jakarta.validation.constraints.NotBlank;

public record OptionCreateRequest(
        @NotBlank String caption
) {}
