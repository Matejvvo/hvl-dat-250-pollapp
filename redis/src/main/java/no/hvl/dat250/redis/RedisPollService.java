package no.hvl.dat250.redis;

import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.repository.interfaces.PollRepo;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;
import no.hvl.dat250.pollapp.repository.interfaces.VoteRepo;
import no.hvl.dat250.pollapp.service.impl.PollServiceImpl;
import no.hvl.dat250.pollapp.service.rabbit.PollAppEventPublisherReal;
import redis.clients.jedis.UnifiedJedis;

import java.time.Clock;
import java.util.UUID;

import static no.hvl.dat250.redis.RedisApplication.REDIS_URL;

public class RedisPollService extends PollServiceImpl {
    public RedisPollService(UserRepo userRepo,
                            PollRepo pollRepo,
                            VoteRepo voteRepo,
                            Clock clock,
                            PollAppEventPublisherReal eventPublisher) {
        super(userRepo, pollRepo, voteRepo, clock, eventPublisher);
    }

    @Override
    public Vote castVote(UUID voterId, UUID pollId, UUID optionId) {
        Vote res = super.castVote(voterId, pollId, optionId);

        UnifiedJedis jedis = new UnifiedJedis(REDIS_URL);
        jedis.hset("poll:" + pollId, "valid", "false");
        jedis.close();

        return res;
    }
}
