package no.hvl.dat250.redis;

import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.repository.*;
import no.hvl.dat250.pollapp.service.impl.PollServiceImpl;
import redis.clients.jedis.UnifiedJedis;

import java.time.Clock;
import java.util.UUID;

import static no.hvl.dat250.redis.RedisApplication.REDIS_URL;

public class RedisPollService extends PollServiceImpl {
    public RedisPollService(UserRepo userRepo,
                            PollRepo pollRepo,
                            VoteRepo voteRepo,
                            Clock clock) {
        super(userRepo, pollRepo, voteRepo, clock);
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
