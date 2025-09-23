package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.domain.*;
import no.hvl.dat250.pollapp.service.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Basic tests

@SpringBootTest
@DirtiesContext
class PollAppApplicationTests {
    @Autowired
    private PollService pollService;
    @Autowired
    private UserService userService;
    @Autowired
    private VoteService voteService;

	@Test
    public void testEndToEndScenarioIT() {
        // Create a new user
        User u1 = userService.create("alice", "alice@example.com", "ignored");
        assertThat(u1).isNotNull();
        assertThat(u1.getIdAsUUID()).isNotNull();

        // List all users
        List<User> usersAfterU1 = userService.list();
        assertThat(usersAfterU1).extracting(User::getIdAsUUID).containsExactly(u1.getIdAsUUID());

        // Create another user
        User u2 = userService.create("bob", "bob@example.com", "ignored");
        assertThat(u2).isNotNull();

        // List all users again
        List<User> usersAfterU2 = userService.list();
        assertThat(usersAfterU2).hasSize(2);
        assertThat(usersAfterU2).extracting(User::getIdAsUUID).contains(u1.getIdAsUUID(), u2.getIdAsUUID());

        // User 1 creates a new poll
        Instant publishedAt = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant validUntil  = Instant.now().plus(1, ChronoUnit.DAYS);

        Poll poll = pollService.create(
                "What is your favorite color?",
                1,
                false,
                u1.getIdAsUUID(),
                publishedAt,
                validUntil,
                List.of("Yellow")
        );
        assertThat(poll).isNotNull();
        assertThat(poll.getIdAsUUID()).isNotNull();

        // Add options explicitly
        VoteOption red   = pollService.addVoteOption(poll.getIdAsUUID(), "Red");
        VoteOption green = pollService.addVoteOption(poll.getIdAsUUID(), "Green");
        assertThat(red).isNotNull();
        assertThat(green).isNotNull();

        // List polls
        List<Poll> polls = pollService.list();
        assertThat(polls).extracting(Poll::getIdAsUUID).contains(poll.getIdAsUUID());

        // User 2 votes on the poll
        Vote firstVote = pollService.castVote(u2.getIdAsUUID(), poll.getIdAsUUID(), red.getIdAsUUID());
        assertThat(firstVote).isNotNull();
        assertThat(firstVote.getVoter().getIdAsUUID()).isEqualTo(u2.getIdAsUUID());
        assertThat(firstVote.getOption().getIdAsUUID()).isEqualTo(red.getIdAsUUID());

        // List votes
        List<Vote> votesNow1 = voteService.list();
        assertThat(votesNow1).hasSize(1);
        assertThat(votesNow1.getFirst().getVoter().getIdAsUUID()).isEqualTo(u2.getIdAsUUID());
        assertThat(votesNow1.getFirst().getOption().getIdAsUUID()).isEqualTo(red.getIdAsUUID());

        // User 2 changes his vote
        Vote updatedVote = voteService.update(firstVote.getIdAsUUID(), u2.getIdAsUUID(), green.getIdAsUUID());
        assertThat(updatedVote).isNotNull();
        assertThat(updatedVote.getIdAsUUID()).isEqualTo(firstVote.getIdAsUUID());
        assertThat(updatedVote.getOption().getIdAsUUID()).isEqualTo(green.getIdAsUUID());

        // List votes
        List<Vote> votesNow2 = voteService.list();
        assertThat(votesNow2).hasSize(1);
        assertThat(votesNow2.getFirst().getVoter().getIdAsUUID()).isEqualTo(u2.getIdAsUUID());
        assertThat(votesNow2.getFirst().getOption().getIdAsUUID()).isEqualTo(green.getIdAsUUID());

        // Try aggregated results
        assertThat(pollService.getAggregatedResults(poll.getIdAsUUID()));

        // Delete the one poll
        pollService.delete(poll.getIdAsUUID());

        // List votes
        List<Vote> votesAfterDelete = voteService.list();
        assertThat(votesAfterDelete).isEmpty();
    }
}
