package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.dto.*;
import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.service.PollService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;

public class PollServiceImpl implements PollService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;

    public PollServiceImpl(UserRepo userRepo, PollRepo pollRepo, VoteRepo voteRepo) {
        this.userRepo = userRepo;
        this.pollRepo = pollRepo;
        this.voteRepo = voteRepo;
    }

    @Override
    public PollDTO create(String q, int maxVotes, boolean priv, UUID userId, Instant publishedAt, Instant validUntil) {
        if (q == null || q.isBlank()) return null;
        if (maxVotes <= 0 || userId == null) return null;
        if (publishedAt == null || validUntil == null || publishedAt.isAfter(validUntil)) return null;

        User creator = userRepo.findById(userId);
        if (creator == null) return null;

        Poll poll = new Poll();
        poll.setId(UUID.randomUUID());
        poll.setQuestion(q.trim());
        poll.setPublishedAt(publishedAt);
        poll.setValidUntil(validUntil);
        poll.setMaxVotesPerUser(maxVotes);
        poll.setIsPrivate(priv);
        poll.setAllowedVoters(new HashSet<>());
        poll.setCreator(creator);
        poll.setOptions(new ArrayList<>());

        creator.getPolls().add(poll);
        poll = pollRepo.save(poll);
        return PollDTO.from(poll);
    }

    @Override
    public List<PollDTO> list() {
        return pollRepo.findAll().stream().map(PollDTO::from).toList();
    }

    @Override
    public PollDTO get(UUID pollId) {
        if (pollId == null) return null;
        return PollDTO.from(pollRepo.findById(pollId));
    }

    @Override
    public PollDTO update(UUID pollId, String q, int maxVotes, boolean priv, Instant validUntil) {
        if (pollId == null) return null;
        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return null;

        if (q != null && !q.isBlank()) poll.setQuestion(q.trim());

        if (maxVotes > poll.getMaxVotesPerUser()) poll.setMaxVotesPerUser(maxVotes);

        poll.setIsPrivate(priv);

        if (validUntil != null && validUntil.isAfter(poll.getValidUntil())) poll.setValidUntil(validUntil);

        poll = pollRepo.save(poll);
        return PollDTO.from(poll);
    }

    @Override
    public void delete(UUID pollId) {
        if (pollId == null) return;
        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return;

        if (poll.getCreator() != null && poll.getCreator().getPolls() != null)
            poll.getCreator().getPolls().remove(poll);

        if (poll.getOptions() != null) for (VoteOption option : poll.getOptions())
            for (Vote vote : option.getVotes())
                voteRepo.deleteById(vote.getId());

        pollRepo.deleteById(pollId);
    }

    @Override
    public VoteOptionDTO addVoteOption(UUID pollId, String caption) {
        if (pollId == null || caption == null || caption.isBlank()) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return null;

        // Check duplicate options
        boolean duplicateExists = poll.getOptions().stream().anyMatch(opt -> caption.trim().equalsIgnoreCase(opt.getCaption().trim()));
        if (duplicateExists) return null;

        int order = poll.getOptions().isEmpty() ? 0 : poll.getOptions().getLast().getPresentationOrder() + 1;

        VoteOption voteOption = new VoteOption();
        voteOption.setId(UUID.randomUUID());
        voteOption.setCaption(caption.trim());
        voteOption.setPresentationOrder(order);
        voteOption.setPoll(poll);
        voteOption.setVotes(new HashSet<>());

        poll.getOptions().add(voteOption);
        return VoteOptionDTO.from(voteOption);
    }

    @Override
    public List<VoteOptionDTO> listVoteOptions(UUID pollId) {
        if (pollId == null) return null;
        return pollRepo.findById(pollId).getOptions().stream().sorted(Comparator.comparing(VoteOption::getPresentationOrder)).map(VoteOptionDTO::from).toList();
    }

    @Override
    public void removeVoteOption(UUID pollId, UUID optionId) {
        if (pollId == null || optionId == null) return;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return;
        VoteOption option = poll.getOptions().stream().filter(opt -> optionId.equals(opt.getId())).findFirst().orElse(null);
        if (option == null) return;

        if (option.getVotes() != null) for (Vote vote : option.getVotes())
            voteRepo.deleteById(vote.getId());

        poll.getOptions().remove(option);
    }

    @Override
    public UserDTO addAllowedVoter(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null || !poll.getIsPrivate()) return null;

        User user = userRepo.findById(userId);
        if (user == null) return null;

        poll.getAllowedVoters().add(user);
        return UserDTO.from(user);
    }

    @Override
    public List<UserDTO> listAllowedVoters(UUID pollId) {
        if (pollId == null) return null;
        return pollRepo.findById(pollId).getAllowedVoters().stream().map(UserDTO::from).toList();
    }

    @Override
    public UserDTO removeAllowedVoter(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null || poll.getAllowedVoters() == null) return null;

        User user = userRepo.findById(userId);
        if (user == null) return null;

        poll.getAllowedVoters().remove(user);

        return UserDTO.from(user);
    }

    @Override
    public String getAggregatedResults(UUID pollId) {
        if (pollId == null) return "Poll not found";
        return "todo results todo";
    }
}
