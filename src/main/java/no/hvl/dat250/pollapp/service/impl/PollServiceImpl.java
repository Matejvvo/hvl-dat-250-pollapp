package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.model.Poll;
import no.hvl.dat250.pollapp.model.User;
import no.hvl.dat250.pollapp.model.Vote;
import no.hvl.dat250.pollapp.model.VoteOption;
import no.hvl.dat250.pollapp.repo.PollRepo;
import no.hvl.dat250.pollapp.repo.UserRepo;
import no.hvl.dat250.pollapp.repo.VoteRepo;
import no.hvl.dat250.pollapp.service.PollService;

import java.time.Instant;
import java.util.*;

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
    public Poll create(String question, int maxVotesPerUser, boolean isPrivate,
                       UUID creatorId, Instant publishedAt, Instant validUntil) {
        if (question == null || question.isBlank()) return null;
        if (maxVotesPerUser <= 0 || creatorId == null) return null;
        if (publishedAt == null || validUntil == null || publishedAt.isAfter(validUntil)) return null;

        User creator = userRepo.findById(creatorId);
        if (creator == null) return null;

        Poll poll = new Poll();
        poll.setId(UUID.randomUUID());
        poll.setQuestion(question.trim());
        poll.setPublishedAt(publishedAt);
        poll.setValidUntil(validUntil);
        poll.setMaxVotesPerUser(maxVotesPerUser);
        poll.setIsPrivate(isPrivate);
        poll.setAllowedVoters(new HashSet<>());
        poll.setCreator(creator);
        poll.setOptions(new ArrayList<>());

        creator.getPolls().add(poll);
        poll = pollRepo.save(poll);
        return poll;
    }

    @Override
    public List<Poll> list() {
        if (pollRepo.empty()) return List.of();
        return pollRepo.findAll().stream().toList();
    }

    @Override
    public Poll get(UUID pollId) {
        if (pollId == null) return null;
        return pollRepo.findById(pollId);
    }

    @Override
    public Poll update(UUID pollId, String question, int maxVotesPerUser, boolean isPrivate, Instant validUntil) {
        if (pollId == null) return null;
        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return null;

        if (question != null && !question.isBlank()) poll.setQuestion(question.trim());

        if (maxVotesPerUser > poll.getMaxVotesPerUser()) poll.setMaxVotesPerUser(maxVotesPerUser);

        poll.setIsPrivate(isPrivate);

        if (validUntil != null && validUntil.isAfter(poll.getValidUntil())) poll.setValidUntil(validUntil);

        poll = pollRepo.save(poll);
        return poll;
    }

    @Override
    public void delete(UUID pollId) {
        if (pollId == null) return;
        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return;

        if (poll.getCreator() != null && poll.getCreator().getPolls() != null)
            poll.getCreator().getPolls().remove(poll);

        if (poll.getOptions() != null) for (VoteOption option : poll.getOptions()) {
            for (Vote vote : option.getVotes()) {
                vote.getVoter().getVotes().remove(vote);
                voteRepo.deleteById(vote.getId());
            }
        }

        pollRepo.deleteById(pollId);
    }

    @Override
    public VoteOption addVoteOption(UUID pollId, String caption) {
        if (pollId == null || caption == null || caption.isBlank()) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return null;

        // Check duplicate options
        boolean duplicateExists = poll.getOptions().stream()
                .anyMatch(opt -> caption.trim().equalsIgnoreCase(opt.getCaption().trim()));
        if (duplicateExists) return null;

        int order = poll.getOptions().isEmpty() ? 0 : poll.getOptions().getLast().getPresentationOrder() + 1;

        VoteOption voteOption = new VoteOption();
        voteOption.setId(UUID.randomUUID());
        voteOption.setCaption(caption.trim());
        voteOption.setPresentationOrder(order);
        voteOption.setPoll(poll);
        voteOption.setVotes(new HashSet<>());

        poll.getOptions().add(voteOption);
        return voteOption;
    }

    @Override
    public List<VoteOption> listVoteOptions(UUID pollId) {
        if (pollId == null) return List.of();
        if (pollRepo.findById(pollId) == null) return List.of();
        return pollRepo.findById(pollId).getOptions().stream()
                .sorted(Comparator.comparing(VoteOption::getPresentationOrder)).toList();
    }

    @Override
    public void removeVoteOption(UUID pollId, UUID optionId) {
        if (pollId == null || optionId == null) return;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return;
        VoteOption option = poll.getOptions().stream()
                .filter(opt -> optionId.equals(opt.getId()))
                .findFirst()
                .orElse(null);
        if (option == null) return;

        if (option.getVotes() != null) for (Vote vote : option.getVotes())
            voteRepo.deleteById(vote.getId());

        poll.getOptions().remove(option);
    }

    @Override
    public User addAllowedVoter(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null || !poll.getIsPrivate()) return null;

        User user = userRepo.findById(userId);
        if (user == null) return null;

        poll.getAllowedVoters().add(user);
        return user;
    }

    @Override
    public List<User> listAllowedVoters(UUID pollId) {
        if (pollId == null) return List.of();
        return pollRepo.findById(pollId).getAllowedVoters().stream().toList();
    }

    @Override
    public User removeAllowedVoter(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null || poll.getAllowedVoters() == null) return null;

        User user = userRepo.findById(userId);
        if (user == null) return null;

        poll.getAllowedVoters().remove(user);

        return user;
    }

    @Override
    public String getAggregatedResults(UUID pollId) {
        if (pollId == null) return "Poll not found";
        return "todo results todo";
    }
}
