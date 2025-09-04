package no.hvl.dat250.pollapp.repo.inmem;

import no.hvl.dat250.pollapp.model.Poll;
import no.hvl.dat250.pollapp.repo.PollRepo;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class PollRepoInMem implements PollRepo {
    private final Map<UUID, Poll> store = new HashMap<>();

    @Override
    public Poll save(Poll poll) {
        if (poll.getId() == null) poll.setId(UUID.randomUUID());
        store.put(poll.getId(), poll);
        return poll;
    }

    @Override
    public Poll findById(UUID id) {
        if (id == null) return null;
        return store.get(id);
    }

    @Override
    public List<Poll> findAll() {
        if (store.isEmpty()) return null;
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return store.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        store.remove(id);
    }
}
