package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.domain.*;
import no.hvl.dat250.pollapp.repository.*;
import no.hvl.dat250.pollapp.service.VoteService;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VoteServiceImpl implements VoteService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;
    private final Clock clock;

    public VoteServiceImpl(UserRepo userRepo,  PollRepo pollRepo, VoteRepo voteRepo,  Clock clock) {
        this.userRepo = userRepo;
        this.pollRepo = pollRepo;
        this.voteRepo = voteRepo;
        this.clock = clock;
    }

    @Override
    public List<Vote> list() {
        if (voteRepo.empty()) return List.of();
        return voteRepo.findAll().stream().toList();
    }

    @Override
    public Vote get(UUID voteId) {
        if (voteId == null) return null;
        if (!voteRepo.existsById(voteId)) return null;
        return voteRepo.findById(voteId);
    }

    @Override
    public Vote update(UUID voteId, UUID userId, UUID optionId) {
        if (userId == null || voteId == null || optionId == null) return null;
        if (!userRepo.existsById(userId) || !voteRepo.existsById(voteId)) return null;

        User user = userRepo.findById(userId);
        Vote vote = voteRepo.findById(voteId);
        if (user == null || vote == null) return null;

        User owner = vote.getVoter();
        if (owner == null || !owner.getId().equals(userId)) return null;

        VoteOption currentOption = vote.getOption();
        if (currentOption == null || currentOption.getPoll() == null) return null;

        Poll poll = currentOption.getPoll();

        // Respect poll time window
        Instant now = Instant.now(clock);
        if (now.isAfter(poll.getValidUntil()) || now.isBefore(poll.getPublishedAt())) return null;

        // Find the new option within the same poll
        VoteOption newOption = poll.getOptions().stream()
                .filter(opt -> optionId.equals(opt.getId()))
                .findFirst()
                .orElse(null);
        if (newOption == null) return null;

        // If unchanged, just return current
        if (currentOption.getId().equals(newOption.getId())) return vote;

        // Prevent multiple votes for the same option by this user (excluding this vote)
        boolean alreadyVotedThisOption = owner.getVotes().stream()
                .anyMatch(v -> !voteId.equals(v.getId())
                        && v.getOption() != null
                        && optionId.equals(v.getOption().getId()));
        if (alreadyVotedThisOption) return null;

        // Update bidirectional relationships
        if (currentOption.getVotes() != null) currentOption.getVotes().remove(vote);
        vote.setOption(newOption);
        if (newOption.getVotes() != null) newOption.getVotes().add(vote);

        // Persist change
        // Ensure owner's vote list contains this vote
        vote = voteRepo.save(vote);
        UUID vid = vote.getId();
        boolean alreadyVoted = owner.getVotes().stream()
                .anyMatch(v -> v.getId().equals(vid));
        if (owner.getVotes() != null && !alreadyVoted)
            owner.getVotes().add(vote);

        return vote;
    }

    @Override
    public void delete(UUID voteId) {
        if (voteId == null || !voteRepo.existsById(voteId)) return;

        Vote vote = voteRepo.findById(voteId);
        if (vote == null) return;

        User owner = vote.getVoter();
        VoteOption option = vote.getOption();
        if (owner == null || option == null) return;

        if (owner.getVotes() != null) owner.getVotes().remove(vote);
        if (option.getVotes() != null) option.getVotes().remove(vote);

        voteRepo.deleteById(voteId);
    }
}
