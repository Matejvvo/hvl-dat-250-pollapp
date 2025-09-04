package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.repo.*;
import no.hvl.dat250.pollapp.repo.inmem.*;
import no.hvl.dat250.pollapp.service.*;
import no.hvl.dat250.pollapp.service.impl.*;
import no.hvl.dat250.pollapp.model.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootApplication
@RestController
public class PollAppApplication {
    private static void printPollVotes(PollService pollService, UUID pollId) {

        System.out.println("  votes by option:");
        for (VoteOption option : pollService.listVoteOptions(pollId))
            if (!option.getVotes().isEmpty())
                System.out.println("    - " + option.getCaption() + " (" + option.getVotes().size() + ")");
        System.out.println("  votes by user:");
        for (VoteOption option : pollService.listVoteOptions(pollId))
            if (!option.getVotes().isEmpty())
                for (Vote vote : option.getVotes())
                    System.out.println("    - " + vote.getVoter().getUsername() + ": " + vote.getOption().getCaption());
    }

    public static void main(String[] args) {
        SpringApplication.run(PollAppApplication.class, args);

        System.out.println("PollAppApplication started...");

        PollRepo pollRepo = new PollRepoInMem();
        UserRepo userRepo = new UserRepoInMem();
        VoteRepo voteRepo = new VoteRepoInMem();

        PollService pollService = new PollServiceImpl(userRepo, pollRepo, voteRepo);
        UserService userService = new UserServiceImpl(userRepo, pollRepo, voteRepo);
        VoteService voteService = new VoteServiceImpl(userRepo, pollRepo, voteRepo);

        // Create a new user
        System.out.println("Created 1st user");
        User u1 = userService.create("Albert", "albert@gmail.com");

        // List all users (-> shows the newly created user)
        System.out.println("Users:");
        for (User user : userService.list())
            System.out.println("  - " + user);

        // Create another user
        System.out.println("Created 2nd user");
        User u2 = userService.create("Bedrich", "beda@me.com");

        // List all users again (-> shows two users)
        System.out.println("Users:");
        for (User user : userService.list())
            System.out.println("  - " + user);

        // User 1 creates a new poll
        System.out.println("Created 1st poll");
        Instant now = Instant.now();
        ZonedDateTime zdt = now.atZone(ZoneId.systemDefault());
        Instant end = zdt.plusMonths(1).toInstant();
        Poll p1 = pollService.create("Kdy sraz?", 1, false, u1.getId(), now, end);
        VoteOption o1 = pollService.addVoteOption(p1.getId(), "po");
        VoteOption o2 = pollService.addVoteOption(p1.getId(), "ut");
        VoteOption o3 = pollService.addVoteOption(p1.getId(), "st");
        VoteOption o4 = pollService.addVoteOption(p1.getId(), "ct");
        VoteOption o5 = pollService.addVoteOption(p1.getId(), "pa");

        // List polls (-> shows the new poll)
        System.out.println("Polls:");
        for (Poll poll : pollService.list())
            System.out.println("  - " + poll);

        // User 2 votes on the poll
        System.out.println("User 2 is voting");
        Vote v1 = voteService.castVote(u2.getId(), p1.getId(), o1.getId());

        // List votes (-> shows the most recent vote for User 2)
        System.out.println("Votes for poll 1:");
        printPollVotes(pollService, p1.getId());

        // User 2 changes his vote
        System.out.println("User 2 is updating his vote");
        Vote v2 = voteService.update(u2.getId(), v1.getId(), o3.getId());

        // List votes (-> shows the most recent vote for User 2)
        System.out.println("Votes for poll 1:");
        printPollVotes(pollService, p1.getId());

        // Delete the one poll
        System.out.println("Poll 1 deleted");
        pollService.delete(p1.getId());

        // List votes (-> empty)
        System.out.println("Votes for poll 1:");
        printPollVotes(pollService, p1.getId());
        System.out.println(userService.list());
        System.out.println(pollService.list());
        System.out.println(voteService.list());

        System.out.println("All tests done!");
    }
}
