package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.dto.*;
import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.service.VoteService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VoteServiceImpl implements VoteService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;

    public VoteServiceImpl(UserRepo userRepo, PollRepo pollRepo, VoteRepo voteRepo) {
        this.userRepo = userRepo;
        this.pollRepo = pollRepo;
        this.voteRepo = voteRepo;
    }

    @Override
    public VoteDTO castVote(UUID voterId, UUID pollId, UUID optionId) {
        if (pollId == null || voterId == null || optionId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        User voter = userRepo.findById(voterId);
        Instant now = Instant.now();
        if (poll == null || voter == null) return null;
        if (now.isAfter(poll.getValidUntil()) || now.isBefore(poll.getPublishedAt())) return null;

        // Check if voter is allowed
        if (poll.getIsPrivate() && !poll.getAllowedVoters().contains(voter)) return null;

        // Find the option within this poll
        VoteOption selectedOption = poll.getOptions().stream().filter(opt -> optionId.equals(opt.getId())).findFirst().orElse(null);
        if (selectedOption == null) return null;

        // Count this voter's votes in this poll
        long userVoteCount = voter.getVotes().stream().filter(v -> v.getOption() != null && v.getOption().getPoll() != null && pollId.equals(v.getOption().getPoll().getId())).count();
        if (userVoteCount >= poll.getMaxVotesPerUser()) return null;

        // Prevent multiple votes for the same option
        boolean alreadyVotedThisOption = voter.getVotes().stream().anyMatch(v -> v.getOption() != null && optionId.equals(v.getOption().getId()));
        if (alreadyVotedThisOption) return null;

        Vote vote = new Vote();
        vote.setId(UUID.randomUUID());
        vote.setPublishedAt(Instant.now());
        vote.setVoter(voter);
        vote.setOption(selectedOption);

        vote = voteRepo.save(vote);
        if (selectedOption.getVotes() == null || voter.getVotes() == null) return null;
        selectedOption.getVotes().add(vote);
        voter.getVotes().add(vote);
        return VoteDTO.from(vote);
    }

    @Override
    public List<VoteDTO> listPollVotes(UUID pollId) {
        if (pollId == null) return null;
        if (!pollRepo.existsById(pollId)) return null;
        return pollRepo.findById(pollId).getOptions().stream().flatMap(opt -> opt.getVotes().stream()).map(VoteDTO::from).toList();
    }

    @Override
    public VoteDTO get(UUID voteId) {
        if (voteId == null) return null;
        if (!voteRepo.existsById(voteId)) return null;
        return VoteDTO.from(voteRepo.findById(voteId));
    }

    @Override
    public VoteDTO update(UUID userId, UUID voteId, UUID optionId) {
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
        Instant now = Instant.now();
        if (now.isAfter(poll.getValidUntil()) || now.isBefore(poll.getPublishedAt())) return null;

        // Find the new option within the same poll
        VoteOption newOption = poll.getOptions().stream().filter(opt -> optionId.equals(opt.getId())).findFirst().orElse(null);
        if (newOption == null) return null;

        // If unchanged, just return current
        if (currentOption.getId().equals(newOption.getId())) return VoteDTO.from(vote);

        // Prevent multiple votes for the same option by this user (excluding this vote)
        boolean alreadyVotedThisOption = owner.getVotes().stream().anyMatch(v -> !voteId.equals(v.getId()) && v.getOption() != null && optionId.equals(v.getOption().getId()));
        if (alreadyVotedThisOption) return null;

        // Update bidirectional relationships
        if (currentOption.getVotes() != null) currentOption.getVotes().remove(vote);
        vote.setOption(newOption);
        if (newOption.getVotes() != null) newOption.getVotes().add(vote);

        // Persist change
        // Ensure owner's vote list contains this vote
        vote = voteRepo.save(vote);
        UUID vid = vote.getId();
        if (owner.getVotes() != null && owner.getVotes().stream().noneMatch(v -> v.getId().equals(vid)))
            owner.getVotes().add(vote);

        return VoteDTO.from(vote);
    }

    @Override
    public void delete(UUID userId, UUID voteId) {
        if (userId == null || voteId == null) return;
        if (!userRepo.existsById(userId) || !voteRepo.existsById(voteId)) return;

        User user = userRepo.findById(userId);
        Vote vote = voteRepo.findById(voteId);
        if (user == null || vote == null) return;

        User owner = vote.getVoter();
        VoteOption option = vote.getOption();

        if (owner == null || option == null || !owner.getId().equals(userId)) return;

        if (owner.getVotes() != null) owner.getVotes().remove(vote);
        if (option.getVotes() != null) option.getVotes().remove(vote);

        voteRepo.deleteById(voteId);
    }
}
