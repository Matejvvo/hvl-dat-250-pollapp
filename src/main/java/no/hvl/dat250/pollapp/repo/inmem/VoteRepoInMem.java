package no.hvl.dat250.pollapp.repo.inmem;

import no.hvl.dat250.pollapp.model.Vote;
import no.hvl.dat250.pollapp.repo.VoteRepo;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class VoteRepoInMem implements VoteRepo {
    private final Map<UUID, Vote> store = new HashMap<>();

    @Override
    public Vote save(Vote vote) {
        if (vote.getId() == null) vote.setId(UUID.randomUUID());
        store.put(vote.getId(), vote);
        return vote;
    }

    @Override
    public Vote findById(UUID id) {
        if (id == null) return null;
        return store.get(id);
    }

    @Override
    public List<Vote> findAll() {
        if (store.isEmpty()) return null;
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return store.containsKey(id);
    }

    @Override
    public boolean empty() {
        return store.isEmpty();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        store.remove(id);
    }
}
