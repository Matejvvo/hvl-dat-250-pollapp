package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.domain.*;
import no.hvl.dat250.pollapp.service.PollService;

import no.hvl.dat250.pollapp.web.dto.OptionCreateRequest;
import no.hvl.dat250.pollapp.web.dto.PollCreateRequest;
import no.hvl.dat250.pollapp.web.dto.PollUpdateRequest;
import no.hvl.dat250.pollapp.web.dto.VoteCreateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/polls")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping
    public Poll create(@RequestBody PollCreateRequest req) {
        return pollService.create(
                req.question(),
                req.maxVotesPerUser(),
                req.isPrivate(),
                req.creatorId(),
                req.publishedAt(),
                req.validUntil(),
                req.options()
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
    public Poll update(@PathVariable UUID id, @RequestBody PollUpdateRequest req) {
        return pollService.update(
                id,
                req.question(),
                req.maxVotesPerUser(),
                req.isPrivate(),
                req.validUntil()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        pollService.delete(id);
    }

    @PostMapping("/{id}/options")
    public VoteOption addVoteOption(@PathVariable UUID id, @RequestBody OptionCreateRequest req) {
        return pollService.addVoteOption(id, req.caption());
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
    public Vote castVote(@PathVariable UUID pollId, @RequestBody VoteCreateRequest req) {
        return pollService.castVote(req.voterId(), pollId, req.optionId());
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
