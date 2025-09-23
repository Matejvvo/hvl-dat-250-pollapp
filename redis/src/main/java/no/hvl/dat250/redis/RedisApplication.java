package no.hvl.dat250.redis;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.repository.PollRepo;
import no.hvl.dat250.pollapp.repository.UserRepo;
import no.hvl.dat250.pollapp.repository.VoteRepo;
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

    static PollRepo polls = new PollRepoInMem();
    static UserRepo users = new UserRepoInMem();
    static VoteRepo votes = new VoteRepoInMem();

    static PollService pollService = new RedisPollService(users, polls, votes, Clock.systemUTC());
    static UserService userService = new UserServiceImpl(users, polls, votes, Clock.systemUTC());
    static VoteService voteService = new VoteServiceImpl(users, polls, votes, Clock.systemUTC());


    public static Poll setData() {
        User a = userService.create("alice", "mail@mail.mail", "");
        User b = userService.create("bob", "mail@mail.mail", "");
        User c = userService.create("charlie", "mail@mail.mail", "");
        User d = userService.create("david", "mail@mail.mail", "");

        Poll p = pollService.create(
                "Pineapple on Pizza", 1, false, a.getId(),
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().plus(10, ChronoUnit.DAYS),
                null);
        VoteOption o1 = p.addVoteOption("Yes, yammy!");
        VoteOption o2 = p.addVoteOption("Mamma mia, nooooo!");
        VoteOption o3 = p.addVoteOption("I do not really care ...");

        pollService.castVote(a.getId(), p.getId(), o1.getId());
        pollService.castVote(b.getId(), p.getId(), o2.getId());
        pollService.castVote(c.getId(), p.getId(), o1.getId());
        pollService.castVote(d.getId(), p.getId(), o3.getId());

        return p;
    }

    public static void castTestVote() {
        voteService.list().getFirst();
        pollService.castVote(
                users.findAll().getFirst().getId(),
                polls.findAll().getFirst().getId(),
                polls.findAll().getFirst().getOptions().getFirst().getId()
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

        String ttl = jedis.hget("poll:" + p.getId(), "ttl");
        String valid = jedis.hget("poll:" + p.getId(), "valid");
        if (ttl == null || ttl.isBlank() || Long.parseLong(ttl) < Instant.now().getEpochSecond() ||
                valid == null || valid.isBlank() || !valid.equals("true")) {
            System.out.println("No poll with title '" + p.getId() + "' found in cache");
            addToCache(p);
        }

        System.out.println(jedis.hgetAll("poll:" + p.getId()));

        jedis.close();
    }

    public static void addToCache(Poll p) {
        UnifiedJedis jedis = new UnifiedJedis(REDIS_URL);

        String ttl = "" + Instant.now().plus(10, ChronoUnit.MINUTES).getEpochSecond();
        jedis.hset("poll:" + p.getId().toString(), "title", p.getQuestion());
        jedis.hset("poll:" + p.getId().toString(), "ttl", ttl);
        jedis.hset("poll:" + p.getId().toString(), "valid", "true");

        for (VoteOption o : p.getOptions()) {
            jedis.hset("poll:" + p.getId().toString(), "option:" + o.getPresentationOrder()
                    + ":caption", o.getCaption());
            jedis.hset("poll:" + p.getId().toString(), "option:" + o.getPresentationOrder()
                    + ":voteCount", "" + o.getVotes().size());
        }

        jedis.close();
    }
}
