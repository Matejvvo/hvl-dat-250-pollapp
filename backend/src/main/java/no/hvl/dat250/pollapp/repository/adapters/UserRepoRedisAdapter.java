package no.hvl.dat250.pollapp.repository.adapters;

import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;
import no.hvl.dat250.pollapp.repository.redis.UserRepoRedis;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Primary
public class UserRepoRedisAdapter implements UserRepo {
    private final UserRepoRedis springData;

    public UserRepoRedisAdapter(UserRepoRedis springData) {
        this.springData = springData;
    }

    @Override
    public User save(User user) {
        if (user.getIdAsUUID() == null) {
            user.setId(UUID.randomUUID());
        }
        return springData.save(user);
    }

    @Override
    public User findById(UUID id) {
        return springData.findById(id.toString()).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return StreamSupport.stream(springData.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return springData.existsById(id.toString());
    }

    @Override
    public boolean empty() {
        return !springData.findAll().iterator().hasNext();
    }

    @Override
    public void deleteById(UUID id) {
        springData.deleteById(id.toString());
    }
}