package no.hvl.dat250.pollapp.repository.redis;

import no.hvl.dat250.pollapp.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepoRedis extends CrudRepository<User, String> {
}
