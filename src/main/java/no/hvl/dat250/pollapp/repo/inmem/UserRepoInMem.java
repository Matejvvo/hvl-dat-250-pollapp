package no.hvl.dat250.pollapp.repo.inmem;

import no.hvl.dat250.pollapp.model.User;
import no.hvl.dat250.pollapp.repo.UserRepo;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class UserRepoInMem implements UserRepo {
    private final Map<UUID, User> store = new HashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) user.setId(UUID.randomUUID());
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        if (id == null) return null;
        return store.get(id);
    }

    @Override
    public List<User> findAll() {
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
