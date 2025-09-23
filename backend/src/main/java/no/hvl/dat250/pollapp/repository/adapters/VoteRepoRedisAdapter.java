package no.hvl.dat250.pollapp.repository.adapters;

import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.repository.interfaces.VoteRepo;
import no.hvl.dat250.pollapp.repository.redis.VoteRepoRedis;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Primary
public class VoteRepoRedisAdapter implements VoteRepo {
    private final VoteRepoRedis springDataRedisRepo;

    public VoteRepoRedisAdapter(VoteRepoRedis springDataRedisRepo) {
        this.springDataRedisRepo = springDataRedisRepo;
    }

    @Override
    public Vote save(Vote vote) {
        if (vote.getIdAsUUID() == null) {
            vote.setId(UUID.randomUUID());
        }
        return springDataRedisRepo.save(vote);
    }

    @Override
    public Vote findById(UUID id) {
        return springDataRedisRepo.findById(id.toString()).orElse(null);
    }

    @Override
    public List<Vote> findAll() {
        return StreamSupport.stream(springDataRedisRepo.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRedisRepo.existsById(id.toString());
    }

    @Override
    public boolean empty() {
        return !springDataRedisRepo.findAll().iterator().hasNext();
    }

    @Override
    public void deleteById(UUID id) {
        springDataRedisRepo.deleteById(id.toString());
    }
}
