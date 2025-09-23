package no.hvl.dat250.redis;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.repository.interfaces.PollRepo;
import no.hvl.dat250.pollapp.repository.interfaces.UserRepo;
import no.hvl.dat250.pollapp.repository.interfaces.VoteRepo;
import no.hvl.dat250.pollapp.repository.inmem.PollRepoInMem;
import no.hvl.dat250.pollapp.repository.inmem.UserRepoInMem;
import no.hvl.dat250.pollapp.repository.inmem.VoteRepoInMem;
import no.hvl.dat250.pollapp.service.PollService;
import no.hvl.dat250.pollapp.service.UserService;
import no.hvl.dat250.pollapp.service.VoteService;
import no.hvl.dat250.pollapp.service.impl.UserServiceImpl;
import no.hvl.dat250.pollapp.service.impl.VoteServiceImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.UnifiedJedis;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class RedisApplication {
    static final String REDIS_URL = "redis://localhost:6379";

    static final PollRepo polls = new PollRepoInMem();
    static final UserRepo users = new UserRepoInMem();
    static final VoteRepo votes = new VoteRepoInMem();

    static final PollService pollService = new RedisPollService(users, polls, votes, Clock.systemUTC());
    static final UserService userService = new UserServiceImpl(users, polls, votes, Clock.systemUTC());
    static final VoteService voteService = new VoteServiceImpl(users, polls, votes, Clock.systemUTC());


    public static Poll setData() {
        User a = userService.create("alice", "mail@mail.mail", "");
        User b = userService.create("bob", "mail@mail.mail", "");
        User c = userService.create("charlie", "mail@mail.mail", "");
        User d = userService.create("david", "mail@mail.mail", "");

        Poll p = pollService.create(
                "Pineapple on Pizza", 1, false, a.getIdAsUUID(),
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().plus(10, ChronoUnit.DAYS),
                null);
        VoteOption o1 = p.addVoteOption("Yes, yammy!");
        VoteOption o2 = p.addVoteOption("Mamma mia, nooooo!");
        VoteOption o3 = p.addVoteOption("I do not really care ...");

        pollService.castVote(a.getIdAsUUID(), p.getIdAsUUID(), o1.getIdAsUUID());
        pollService.castVote(b.getIdAsUUID(), p.getIdAsUUID(), o2.getIdAsUUID());
        pollService.castVote(c.getIdAsUUID(), p.getIdAsUUID(), o1.getIdAsUUID());
        pollService.castVote(d.getIdAsUUID(), p.getIdAsUUID(), o3.getIdAsUUID());

        return p;
    }

    public static void castTestVote() {
        voteService.list().getFirst();
        pollService.castVote(
                users.findAll().getFirst().getIdAsUUID(),
                polls.findAll().getFirst().getIdAsUUID(),
                polls.findAll().getFirst().getOptions().getFirst().getIdAsUUID()
        );
    }

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
        System.out.println("Hello Redis!");
        Poll p = setData();

        System.out.println("Try 1:");
        getAggregateVotes(p);
        System.out.println("Try 2:");
        getAggregateVotes(p);
        System.out.println("Try 3:");
        castTestVote();
        getAggregateVotes(p);
	}

    public static void getAggregateVotes(Poll p) {
        UnifiedJedis jedis = new UnifiedJedis(REDIS_URL);

        String ttl = jedis.hget("poll:" + p.getIdAsUUID(), "ttl");
        String valid = jedis.hget("poll:" + p.getIdAsUUID(), "valid");
        if (ttl == null || ttl.isBlank() || Long.parseLong(ttl) < Instant.now().getEpochSecond() ||
                valid == null || valid.isBlank() || !valid.equals("true")) {
            System.out.println("No poll with title '" + p.getIdAsUUID() + "' found in cache");
            addToCache(p);
        }

        System.out.println(jedis.hgetAll("poll:" + p.getIdAsUUID()));

        jedis.close();
    }

    public static void addToCache(Poll p) {
        UnifiedJedis jedis = new UnifiedJedis(REDIS_URL);

        String ttl = "" + Instant.now().plus(10, ChronoUnit.MINUTES).getEpochSecond();
        jedis.hset("poll:" + p.getIdAsUUID(), "title", p.getQuestion());
        jedis.hset("poll:" + p.getIdAsUUID(), "ttl", ttl);
        jedis.hset("poll:" + p.getIdAsUUID(), "valid", "true");

        for (VoteOption o : p.getOptions()) {
            jedis.hset("poll:" + p.getIdAsUUID(), "option:" + o.getPresentationOrder()
                    + ":caption", o.getCaption());
            jedis.hset("poll:" + p.getIdAsUUID(), "option:" + o.getPresentationOrder()
                    + ":voteCount", "" + o.getVotes().size());
        }

        jedis.close();
    }
}
