package no.hvl.dat250.pollapp.web.req;

public record UserUpdateRequest(
        String username,
        String email
) {}
