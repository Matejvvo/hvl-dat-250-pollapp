package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.dto.*;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class PollManager {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Poll> polls = new HashMap<>();

    public PollManager() {
    }

    public UserDTO createNewUser(String username, String email) {
        if (username == null || username.isBlank()) return null;
        if (email == null || email.isBlank()) return null;

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPolls(new HashSet<>());
        user.setVotes(new HashSet<>());

        users.put(user.getId(), user);
        return UserDTO.from(user);
    }

    public PollDTO createNewPoll(String question, int maxVotesPerUser, boolean isPrivate,
                              UUID creatorId, Instant publishedAt, Instant validUntil) {
        if (question == null || question.isBlank()) return null;
        if (maxVotesPerUser <= 0 || creatorId == null) return null;
        if (publishedAt == null || validUntil == null || publishedAt.isAfter(validUntil)) return null;

        User creator = users.get(creatorId);
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
        polls.put(poll.getId(), poll);
        return PollDTO.from(poll);
    }

    public boolean addVoterToPoll(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return false;

        Poll poll = polls.get(pollId);
        if (poll == null || !poll.getIsPrivate()) return false;

        User user = users.get(userId);
        if (user == null) return false;

        poll.getAllowedVoters().add(user);
        return true;
    }

    public VoteOptionDTO addOptionToPoll(UUID pollId, String caption) {
        if (pollId == null || caption == null || caption.isBlank()) return null;

        Poll poll = polls.get(pollId);
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
        return VoteOptionDTO.from(voteOption);
    }

    public VoteDTO castVote(UUID pollId, UUID voterId, UUID optionId) {
        if (pollId == null || voterId == null || optionId == null) return null;

        Poll poll = polls.get(pollId);
        User voter = users.get(voterId);
        Instant now = Instant.now();
        if (poll == null || voter == null) return null;
        if (now.isAfter(poll.getValidUntil()) || now.isBefore(poll.getPublishedAt())) return null;

        // Check if voter is allowed
        if (poll.getIsPrivate() && !poll.getAllowedVoters().contains(voter)) return null;

        // Find the option within this poll
        VoteOption selectedOption = poll.getOptions().stream()
                .filter(opt -> optionId.equals(opt.getId()))
                .findFirst()
                .orElse(null);
        if (selectedOption == null) return null;

        // Count this voter's votes in this poll
        long userVoteCount = voter.getVotes().stream()
                .filter(v -> v.getOption() != null
                        && v.getOption().getPoll() != null
                        && pollId.equals(v.getOption().getPoll().getId()))
                .count();
        if (userVoteCount >= poll.getMaxVotesPerUser()) return null;

        // Prevent multiple votes for the same option
        boolean alreadyVotedThisOption = voter.getVotes().stream()
                .anyMatch(v -> v.getOption() != null && optionId.equals(v.getOption().getId()));
        if (alreadyVotedThisOption) return null;

        Vote vote = new Vote();
        vote.setId(UUID.randomUUID());
        vote.setPublishedAt(Instant.now());
        vote.setVoter(voter);
        vote.setOption(selectedOption);

        selectedOption.getVotes().add(vote);
        voter.getVotes().add(vote);
        return VoteDTO.from(vote);
    }
}
