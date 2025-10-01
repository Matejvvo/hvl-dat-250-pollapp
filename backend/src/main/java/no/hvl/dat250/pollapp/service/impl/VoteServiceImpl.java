package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;
import no.hvl.dat250.pollapp.repository.interfaces.VoteRepo;
import no.hvl.dat250.pollapp.service.interfaces.VoteService;

import no.hvl.dat250.pollapp.service.rabbit.PollEventPublisher;
import no.hvl.dat250.pollapp.service.rabbit.VoteEventDTO;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VoteServiceImpl implements VoteService {
    private final UserRepo userRepo;
    private final VoteRepo voteRepo;
    private final Clock clock;
    private final PollEventPublisher publisher;

    public VoteServiceImpl(UserRepo userRepo, VoteRepo voteRepo,  Clock clock,  PollEventPublisher publisher) {
        this.userRepo = userRepo;
        this.voteRepo = voteRepo;
        this.clock = clock;
        this.publisher = publisher;
    }

    @Transactional
    @Override
    public List<Vote> list() {
        if (voteRepo.empty()) return List.of();
        return voteRepo.findAll().stream().toList();
    }

    @Transactional
    @Override
    public Vote get(UUID voteId) {
        if (voteId == null) return null;
        if (!voteRepo.existsById(voteId)) return null;
        return voteRepo.findById(voteId);
    }

    @Transactional
    @Override
    public Vote update(UUID voteId, UUID userId, UUID optionId) {
        if (userId == null || voteId == null || optionId == null) return null;
        if (!userRepo.existsById(userId) || !voteRepo.existsById(voteId)) return null;

        User user = userRepo.findById(userId);
        Vote vote = voteRepo.findById(voteId);
        if (user == null || vote == null) return null;

        User owner = vote.getVoter();
        if (owner == null || !owner.getIdAsUUID().equals(userId)) return null;

        VoteOption currentOption = vote.getOption();
        if (currentOption == null || currentOption.getPoll() == null) return null;

        Poll poll = currentOption.getPoll();

        // Respect poll time window
        Instant now = Instant.now(clock);
        if (now.isAfter(poll.getValidUntil()) || now.isBefore(poll.getPublishedAt())) return null;

        // Find the new option within the same poll
        VoteOption newOption = poll.getOptions().stream()
                .filter(opt -> optionId.equals(opt.getIdAsUUID()))
                .findFirst()
                .orElse(null);
        if (newOption == null) return null;

        // If unchanged, just return current
        if (currentOption.getIdAsUUID().equals(newOption.getIdAsUUID())) return vote;

        // Prevent multiple votes for the same option by this user (excluding this vote)
        boolean alreadyVotedThisOption = owner.getVotes().stream()
                .anyMatch(v -> !voteId.equals(v.getIdAsUUID())
                        && v.getOption() != null
                        && optionId.equals(v.getOption().getIdAsUUID()));
        if (alreadyVotedThisOption) return null;

        // Update bidirectional relationships
        if (currentOption.getVotes() != null) currentOption.getVotes().remove(vote);
        vote.setOption(newOption);
        if (newOption.getVotes() != null) newOption.getVotes().add(vote);

        // Persist change
        // Ensure owner's vote list contains this vote
        vote = voteRepo.save(vote);
        UUID vid = vote.getIdAsUUID();
        boolean alreadyVoted = owner.getVotes().stream()
                .anyMatch(v -> v.getIdAsUUID().equals(vid));
        if (owner.getVotes() != null && !alreadyVoted)
            owner.getVotes().add(vote);

        VoteEventDTO voteEventDTO = new VoteEventDTO(vote);
        publisher.publishVoteUpdated(voteEventDTO);  // Publish event
        return vote;
    }

    @Transactional
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

        VoteEventDTO voteEventDTO = new VoteEventDTO(vote);
        publisher.publishVoteRemove(voteEventDTO);  // Publish event
        voteRepo.deleteById(voteId);
    }
}
