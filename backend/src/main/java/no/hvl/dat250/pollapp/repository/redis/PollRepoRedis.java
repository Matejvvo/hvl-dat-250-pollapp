package no.hvl.dat250.pollapp.repository.redis;

import no.hvl.dat250.pollapp.domain.Poll;
import org.springframework.data.repository.CrudRepository;

public interface PollRepoRedis extends CrudRepository<Poll, String> {
}
