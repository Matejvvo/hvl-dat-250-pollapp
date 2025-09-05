package no.hvl.dat250.pollapp.service.impl;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.service.PollService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class PollServiceImpl implements PollService {
    private final UserRepo userRepo;
    private final PollRepo pollRepo;
    private final VoteRepo voteRepo;

    public PollServiceImpl(UserRepo userRepo,  PollRepo pollRepo, VoteRepo voteRepo) {
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
    public void removeAllowedVoter(UUID pollId, UUID userId) {
        if (pollId == null || userId == null) return;

        Poll poll = pollRepo.findById(pollId);
        if (poll == null || poll.getAllowedVoters() == null) return;

        User user = userRepo.findById(userId);
        if (user == null) return;

        poll.getAllowedVoters().remove(user);
    }

    @Override
    public Vote castVote(UUID voterId, UUID pollId, UUID optionId) {
        if (pollId == null || voterId == null || optionId == null) return null;

        Poll poll = pollRepo.findById(pollId);
        User voter = userRepo.findById(voterId);
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

        vote = voteRepo.save(vote);
        if (selectedOption.getVotes() == null) selectedOption.setVotes(new HashSet<>());
        if(voter.getVotes() == null) voter.setVotes(new HashSet<>());
        selectedOption.getVotes().add(vote);
        voter.getVotes().add(vote);
        return vote;
    }

    @Override
    public List<Vote> listPollVotes(UUID pollId) {
        if (pollId == null) return List.of();
        if (!pollRepo.existsById(pollId)) return List.of();
        return pollRepo.findById(pollId).getOptions().stream()
                .flatMap(opt -> opt.getVotes().stream()).toList();
    }

    @Override
    // todo
    public String getAggregatedResults(UUID pollId) {
        if (pollId == null) return "{\"error\":\"Poll not found\"}";

        Poll poll = pollRepo.findById(pollId);
        if (poll == null) return "{\"error\":\"Poll not found\"}";

        List<VoteOption> options = poll.getOptions() == null ? List.of() : poll.getOptions();
        options = options.stream()
                .sorted(Comparator.comparing(VoteOption::getPresentationOrder))
                .toList();

        int totalVotes = 0;
        for (VoteOption opt : options) {
            if (opt.getVotes() != null) totalVotes += opt.getVotes().size();
        }

        StringBuilder sb = new StringBuilder(512);
        sb.append('{')
                .append("\"pollId\":\"").append(poll.getId()).append("\",")
                .append("\"question\":\"").append(jsonEscape(poll.getQuestion())).append("\",")
                .append("\"isPrivate\":").append(poll.getIsPrivate()).append(',')
                .append("\"publishedAt\":\"").append(poll.getPublishedAt() != null ? poll.getPublishedAt().toString() : "").append("\",")
                .append("\"validUntil\":\"").append(poll.getValidUntil() != null ? poll.getValidUntil().toString() : "").append("\",")
                .append("\"totalVotes\":").append(totalVotes).append(',');

        sb.append("\"options\":[");
        boolean first = true;
        for (VoteOption opt : options) {
            if (!first) sb.append(',');
            first = false;

            List<Vote> votes = (opt.getVotes() == null) ? List.of() : opt.getVotes().stream().toList();
            int count = votes.size();
            double pct = (totalVotes == 0) ? 0.0 : (count * 100.0) / totalVotes;
            pct = Math.round(pct * 100.0) / 100.0;

            sb.append('{')
                    .append("\"optionId\":\"").append(opt.getId()).append("\",")
                    .append("\"caption\":\"").append(jsonEscape(opt.getCaption())).append("\",")
                    .append("\"order\":").append(opt.getPresentationOrder()).append(',')
                    .append("\"votes\":").append(count).append(',')
                    .append("\"percentage\":").append(pct).append(',');

            // --- voters array ---
            sb.append("\"voters\":[");
            boolean firstVoter = true;
            for (Vote v : votes) {
                if (!firstVoter) sb.append(',');
                firstVoter = false;
                sb.append(serializeVoter(v));
            }
            sb.append(']');

            sb.append('}');
        }
        sb.append("],");

        sb.append("\"computedAt\":\"").append(Instant.now().toString()).append("\"");
        sb.append('}');

        return sb.toString();
    }

    private String serializeVoter(Vote v) {
        String userId = "";
        String displayName = "";
        String votedAt = "";

        if (v != null) {
            // Try user object first
            User u = v.getVoter();
            if (u != null) {
                if (u.getId() != null) userId = u.getId().toString();
                if (u.getUsername() != null) displayName = u.getUsername();
                else if (u.getEmail() != null) displayName = u.getEmail();
                else displayName = u.getId().toString();
            }
            // Fallback: direct userId on Vote (if your model has it)
            if (userId.isEmpty() && v.getId() != null) {
                userId = v.getId().toString();
            }
            // Timestamp (createdAt / votedAt depending on your model)
            if (v.getPublishedAt() != null) votedAt = v.getPublishedAt().toString();
        }

        return '{' + "\"userId\":\"" + jsonEscape(userId) + "\","
                        + "\"displayName\":\"" + jsonEscape(displayName) + "\","
                        + "\"votedAt\":\"" + jsonEscape(votedAt) + "\"" +
                '}';
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b");  break;
                case '\f': out.append("\\f");  break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
