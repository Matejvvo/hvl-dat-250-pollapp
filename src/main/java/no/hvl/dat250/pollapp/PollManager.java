package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.model.Poll;
import no.hvl.dat250.pollapp.model.User;
import no.hvl.dat250.pollapp.model.Vote;
import no.hvl.dat250.pollapp.model.VoteOption;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PollManager {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Poll> polls = new HashMap<>();
    private final Map<UUID, Vote> votes = new HashMap<>();
    private final Map<UUID, VoteOption> options = new HashMap<>();

    public PollManager() {
    }

    public User addUser(User user) {
        if (user.getId() == null) user.setId(UUID.randomUUID());
        users.put(user.getId(), user);
        return user;
    }

    public User getUser(UUID id) {
        return users.get(id);
    }

    public Poll addPoll(Poll poll) {
        if (poll.getId() == null) poll.setId(UUID.randomUUID());
        polls.put(poll.getId(), poll);
        return poll;
    }

    public Poll getPoll(UUID id) {
        return polls.get(id);
    }

    public VoteOption addOption(VoteOption option) {
        if (option.getId() == null) option.setId(UUID.randomUUID());
        options.put(option.getId(), option);
        return option;
    }

    public VoteOption getOption(UUID id) {
        return options.get(id);
    }

    public Vote addVote(Vote vote) {
        if (vote.getId() == null) vote.setId(UUID.randomUUID());
        votes.put(vote.getId(), vote);
        return vote;
    }

    public Vote getVote(UUID id) {
        return votes.get(id);
    }
}
