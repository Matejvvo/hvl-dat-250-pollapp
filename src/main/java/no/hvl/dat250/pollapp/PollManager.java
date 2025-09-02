package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.dto.*;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PollManager {
    // --- Manager ---
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Poll> polls = new HashMap<>();
    public PollManager() {
        return;
    }

    // --- Factory Methods ---
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

    // --- CRUD Poll ---
    public PollDTO createPoll(PollDTO poll);
    public List<PollDTO> listPolls();
    public PollDTO getPoll(UUID pollId);
    public PollDTO updatePoll(UUID pollId, PollDTO poll);
    public PollDTO deletePoll(UUID pollId);
    public String getPollAggregatedResults(UUID pollId);

    // --- CRUD Poll VoteOption ---
    public VoteOptionDTO addPollVoteOption(UUID pollId, VoteOptionDTO option);
    public List<VoteOptionDTO> listPollVoteOptions(UUID pollId);
    public VoteOptionDTO updatePollVoteOption(UUID pollId, UUID optionId, VoteOptionDTO option);
    public VoteOptionDTO deletePollVoteOption(UUID pollId, UUID optionId);

    // --- CRUD Poll Allowed Voters ---
    public UserDTO addAllowedVoter(UUID pollId, UUID userId);
    public List<UserDTO> listAllowedVoters(UUID pollId);
    public UserDTO removeAllowedVoter(UUID pollId, UUID userId);

    // --- CRUD User ---
    public UserDTO createUser(UserDTO user);
    public List<UserDTO> listUsers();
    public UserDTO getUser(UUID userId);
    public UserDTO updateUser(UUID userId, UserDTO user);
    public UserDTO deleteUser(UUID userId);

    // --- CRUD User Polls & Votes ---
    public List<PollDTO> listUserPolls(UUID userId);
    public List<VoteDTO> listUserVotes(UUID userId);

    // --- CRUD Vote ---
    public VoteDTO castVote(UUID userId, UUID pollId, VoteDTO vote);
    public List<VoteDTO> listPollVotes(UUID pollId);
    public VoteDTO getVote(UUID voteId);
    public VoteDTO updateUserVote(UUID userId, UUID voteId, VoteDTO voteDto);
    public VoteDTO deleteUserVote(UUID userId, UUID voteId);

    // --- Endpoints ---
    /*
    •	POST    /api/polls → createPoll
	•	GET     /api/polls → listPolls
	•	GET     /api/polls/{pollId} → getPoll
	•	PATCH   /api/polls/{pollId} → updatePoll
	•	DELETE  /api/polls/{pollId} → deletePoll
	•	GET     /api/polls/{pollId}/results → show aggregated results
	•	POST    /api/polls/{pollId}/options → addPollVoteOption
	•	GET     /api/polls/{pollId}/options → listPollVoteOptions
	•	PATCH   /api/polls/{pollId}/options/{optionId} → updatePollVoteOption
	•	DELETE  /api/polls/{pollId}/options/{optionId} → deletePollVoteOption
	•	POST    /api/polls/{pollId}/allowed-voters → addAllowedVoter
	•	GET     /api/polls/{pollId}/allowed-voters → listAllowedVoters
	•	DELETE /api/polls/{pollId}/allowed-voters/{userId} → removeAllowedVoter
	•	POST    /api/polls/{pollId}/votes → castVote
	•	GET     /api/polls/{pollId}/votes → listPollVotes
	•	GET     /api/votes/{voteId} → getVote
	•	PATCH   /api/votes/{voteId} → updateUserVote (or updateVote)
	•	DELETE  /api/votes/{voteId} → deleteUserVote (or deleteVote)
	•	GET     /api/users/{userId}/polls → listUserPolls
	•	GET     /api/users/{userId}/votes → listUserVotes
	•	Users CRUD under
	            /api/users/...
     */
}
