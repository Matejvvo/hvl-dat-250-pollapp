package no.hvl.dat250.pollapp.dto;

import no.hvl.dat250.pollapp.model.User;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserDTO(
        UUID id,
        String username,
        String email,
        Set<PollDTO> polls,
        Set<VoteDTO> votes
) {
    public static UserDTO from(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPolls().stream()
                        .map(p -> PollDTO.from(p, false)) // shallow to avoid recursion
                        .collect(Collectors.toSet()),
                user.getVotes().stream()
                        .map(v -> VoteDTO.from(v, false))
                        .collect(Collectors.toSet())
        );
    }
}
