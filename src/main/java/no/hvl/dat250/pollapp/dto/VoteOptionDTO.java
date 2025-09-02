package no.hvl.dat250.pollapp.dto;

import no.hvl.dat250.pollapp.model.VoteOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record VoteOptionDTO(
        UUID id,
        String caption,
        int presentationOrder,
        PollDTO poll,
        Set<VoteDTO> votes
) {
    public static VoteOptionDTO from(VoteOption option) {
        return from(option, true);
    }

    static VoteOptionDTO from(VoteOption option, boolean deep) {
        if (option == null) return null;
        return new VoteOptionDTO(
                option.getId(),
                option.getCaption(),
                option.getPresentationOrder(),
                deep ? PollDTO.from(option.getPoll(), false) : null,
                option.getVotes().stream()
                        .map(v -> VoteDTO.from(v, false))
                        .collect(Collectors.toSet())
        );
    }
}
