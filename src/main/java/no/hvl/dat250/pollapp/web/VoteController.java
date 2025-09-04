package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.model.Vote;
import no.hvl.dat250.pollapp.service.VoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("")
    public List<Vote> list() {
        return voteService.list();
    }

    @GetMapping("/{id}")
    public Vote get(@PathVariable UUID id) {
        return voteService.get(id);
    }

    @PutMapping("/{id}")
    public Vote update(@PathVariable UUID id, @RequestBody Vote vote) {
        return voteService.update(id, vote.getVoter().getId(), vote.getOption().getId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        voteService.delete(id);
    }
}
