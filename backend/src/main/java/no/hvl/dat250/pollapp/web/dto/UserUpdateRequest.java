package no.hvl.dat250.pollapp.web.dto;

public record UserUpdateRequest(
        String username,
        String email
) {}
