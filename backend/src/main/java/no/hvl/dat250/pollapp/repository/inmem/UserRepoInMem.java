package no.hvl.dat250.pollapp.repository.inmem;

import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Repository
public class UserRepoInMem implements UserRepo {
    private final Map<UUID, User> store = new HashMap<>();

    @Override
    public User save(User user) {
        if (user.getIdAsUUID() == null) user.setId(UUID.randomUUID());
        store.put(user.getIdAsUUID(), user);
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
    public boolean empty() {
        return store.isEmpty();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        store.remove(id);
    }
}
