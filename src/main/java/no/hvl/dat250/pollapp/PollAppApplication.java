package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.dto.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@SpringBootApplication
@RestController
public class PollAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(PollAppApplication.class, args);

        PollManager man = new PollManager();

        /*


        // Create a new user
        UserDTO u1 = man.createNewUser("Albert", "albert@mail.com");
        System.out.println("Created user: " + u1);

        // List all users (-> shows the newly created user)
        System.out.println("All users:");
        for (UserDTO u : man.getAllUsers())
            System.out.println("  - " + u);

        // Create another user
        UserDTO u2 = man.createNewUser("Bedrich", "bedrich@me.com");
        System.out.println("Created user: " + u2);

        // List all users again (-> shows two users)
        System.out.println("All users:");
        for (UserDTO u : man.getAllUsers())
            System.out.println("  - " + u);

        // User 1 creates a new poll
        PollDTO p1 = man.createNewPoll("Kdy dame sraz?", 1, false, u1.id(),
                Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));
        man.addOptionToPoll(p1.id(), "po");
        man.addOptionToPoll(p1.id(), "ut");
        man.addOptionToPoll(p1.id(), "st");
        man.addOptionToPoll(p1.id(), "ct");
        man.addOptionToPoll(p1.id(), "pa");
        System.out.println("Created poll: " + p1);

        // List polls (-> shows the new poll)
        System.out.println("All polls:");
        for (PollDTO p : man.getAllPolls())
            System.out.println("  - " + p);

        // User 2 votes on the poll
        VoteOptionDTO o1 = man.getVoteOptionsForPoll(p1.id()).getFirst();
        VoteDTO v1 = man.castVote(p1.id(), u2.id(), o1.id());
        System.out.println("Casted vote: " + v1);

        // User 2 changes his vote
        // todo

        // List votes (-> shows the most recent vote for User 2)
        // todo

        // Delete the one poll
        // todo

        // List votes (-> empty)
        // todo

        */

    }
}
