package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.model.*;
import no.hvl.dat250.pollapp.service.PollService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping
    public Poll create(@RequestBody Poll poll) {
        return pollService.create(
                poll.getQuestion(),
                poll.getMaxVotesPerUser(),
                poll.getIsPrivate(),
                poll.getCreator().getId(),
                poll.getPublishedAt(),
                poll.getValidUntil()
        );
    }

    @GetMapping
    public List<Poll> list() {
        return pollService.list();
    }

    @GetMapping("/{id}")
    Poll get(@PathVariable UUID id) {
        return pollService.get(id);
    }

    @PutMapping("/{id}")
    public Poll update(@PathVariable UUID id, @RequestBody Poll poll) {
        return pollService.update(
                id,
                poll.getQuestion(),
                poll.getMaxVotesPerUser(),
                poll.getIsPrivate(),
                poll.getValidUntil()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        pollService.delete(id);
    }

    @PostMapping("/{id}/options")
    public VoteOption addVoteOption(@PathVariable UUID id, @RequestBody String caption) {
        return pollService.addVoteOption(id, caption);
    }

    @GetMapping("/{id}/options")
    public List<VoteOption> listVoteOptions(@PathVariable UUID id) {
        return pollService.listVoteOptions(id);
    }

    @DeleteMapping("/{id}/options/{optionId}")
    public void removeVoteOption(@PathVariable UUID id, @PathVariable UUID optionId) {
        pollService.removeVoteOption(id, optionId);
    }

    @PostMapping("/{id}/voters/{voterId}")
    public User addAllowedVoter(@PathVariable UUID id, @PathVariable UUID voterId) {
        return pollService.addAllowedVoter(id, voterId);
    }

    @GetMapping("/{id}/voters")
    public List<User> listAllowedVoters(@PathVariable UUID id) {
        return pollService.listAllowedVoters(id);
    }

    @DeleteMapping("/{id}/voters/{voterId}")
    public void removeAllowedVoter(@PathVariable UUID id, @PathVariable UUID voterId) {
        pollService.removeAllowedVoter(id, voterId);
    }

    @PostMapping("/{pollId}/votes")
    public Vote castVote(@PathVariable UUID pollId, @RequestBody Vote vote) {
        return pollService.castVote(vote.getVoter().getId(), pollId, vote.getOption().getId());
    }

    @GetMapping("/{pollId}/votes")
    public List<Vote> listPollVotes(@PathVariable UUID pollId) {
        return pollService.listPollVotes(pollId);
    }

    @GetMapping("/{id}/results")
    public String getAggregatedResults(@PathVariable UUID id) {
        return pollService.getAggregatedResults(id);
    }
}
