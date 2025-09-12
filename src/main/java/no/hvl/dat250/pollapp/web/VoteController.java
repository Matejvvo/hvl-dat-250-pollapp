package no.hvl.dat250.pollapp.web;

import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.service.VoteService;
import no.hvl.dat250.pollapp.web.dto.VoteUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
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
    public Vote update(@PathVariable UUID id, @RequestBody VoteUpdateRequest req) {
        return voteService.update(id, req.voterId(), req.optionId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        voteService.delete(id);
    }
}
