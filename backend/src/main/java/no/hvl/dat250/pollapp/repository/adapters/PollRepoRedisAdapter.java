package no.hvl.dat250.pollapp.repository.adapters;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.repository.interfaces.PollRepo;
import no.hvl.dat250.pollapp.repository.redis.PollRepoRedis;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Primary
public class PollRepoRedisAdapter implements PollRepo {
    private final PollRepoRedis springData;

    public PollRepoRedisAdapter(PollRepoRedis springData) {
        this.springData = springData;
    }

    @Override
    public Poll save(Poll poll) {
        if (poll.getIdAsUUID() == null) {
            poll.setId(UUID.randomUUID());
        }
        return springData.save(poll);
    }

    @Override
    public Poll findById(UUID id) {
        return springData.findById(id.toString()).orElse(null);
    }

    @Override
    public List<Poll> findAll() {
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