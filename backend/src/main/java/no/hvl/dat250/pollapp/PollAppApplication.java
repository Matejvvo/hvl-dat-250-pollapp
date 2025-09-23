package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.service.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
@RestController
public class PollAppApplication {
    private static ConfigurableApplicationContext context;
    private final PollService p;
    private final UserService u;
    private final VoteService v;

    public PollAppApplication(PollService p, UserService u, VoteService v) {
        this.p = p;
        this.u = u;
        this.v = v;
    }

    public static void main(String[] args) {
        context = SpringApplication.run(PollAppApplication.class, args);
        System.out.println("PollAppApplication started (front end + back end)...");
    }

    @GetMapping("/shutdown")
    public String shutdown() {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
        return "Application is shutting down...";
    }

    @GetMapping("/init")
    public String init() {
        if (u.list().isEmpty() && p.list().isEmpty() && v.list().isEmpty()) {
            UUID aliceId = u.create("Alice", "alice@me.com", "").getIdAsUUID();
            UUID bobId = u.create("Bob", "bob@me.com", "").getIdAsUUID();
            UUID charlieId = u.create("Charlie", "charlie@me.com", "").getIdAsUUID();

            UUID pollPizzaId = p.create("Ananas on pizza?", 1, false, aliceId, Instant.now(), Instant.now().plus(20, ChronoUnit.DAYS), Arrays.asList("yes", "no", "what?!")).getIdAsUUID();
            UUID pollTestId = p.create("Test poll?", 1, false, bobId, Instant.now(), Instant.now().plus(20, ChronoUnit.DAYS), Arrays.asList("1", "2", "asdf", "3")).getIdAsUUID();

            System.out.println(p.listVoteOptions(pollPizzaId));
            System.out.println(p.get(pollPizzaId));

            p.castVote(aliceId, pollPizzaId, p.listVoteOptions(pollPizzaId).get(0).getIdAsUUID());
            p.castVote(bobId, pollPizzaId, p.listVoteOptions(pollPizzaId).get(2).getIdAsUUID());

            p.castVote(bobId, pollTestId, p.listVoteOptions(pollTestId).get(1).getIdAsUUID());
            p.castVote(charlieId, pollTestId, p.listVoteOptions(pollTestId).get(1).getIdAsUUID());
        }
        return "OK";
    }
}
