package no.hvl.dat250.pollapp.repository.inmem;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.repository.interfaces.PollRepo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Repository
public class PollRepoInMem implements PollRepo {
    private final Map<UUID, Poll> store = new HashMap<>();

    @Override
    public Poll save(Poll poll) {
        if (poll.getIdAsUUID() == null) poll.setId(UUID.randomUUID());
        store.put(poll.getIdAsUUID(), poll);
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
    public boolean empty() {
        return store.isEmpty();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        store.remove(id);
    }
}
