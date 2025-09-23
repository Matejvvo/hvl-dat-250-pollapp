package no.hvl.dat250.pollapp.repository.redis;

import no.hvl.dat250.pollapp.domain.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepoRedis extends CrudRepository<Vote, String> {
}
