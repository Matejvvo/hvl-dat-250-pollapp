package no.hvl.dat250.pollapp.service;

import no.hvl.dat250.pollapp.dto.PollDTO;
import no.hvl.dat250.pollapp.dto.UserDTO;
import no.hvl.dat250.pollapp.dto.VoteDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO create(String username, String email);

    List<UserDTO> list();

    UserDTO get(UUID userId);

    UserDTO update(UUID userId, String username, String email);

    void delete(UUID userId);

    List<PollDTO> listPolls(UUID userId);

    List<VoteDTO> listVotes(UUID userId);
}
