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
        assertThat(u1.getId()).isNotNull();

        // List all users
        List<User> usersAfterU1 = userService.list();
        assertThat(usersAfterU1).extracting(User::getId).containsExactly(u1.getId());

        // Create another user
        User u2 = userService.create("bob", "bob@example.com", "ignored");
        assertThat(u2).isNotNull();

        // List all users again
        List<User> usersAfterU2 = userService.list();
        assertThat(usersAfterU2).hasSize(2);
        assertThat(usersAfterU2).extracting(User::getId).contains(u1.getId(), u2.getId());

        // User 1 creates a new poll
        Instant publishedAt = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant validUntil  = Instant.now().plus(1, ChronoUnit.DAYS);

        Poll poll = pollService.create(
                "What is your favorite color?",
                1,
                false,
                u1.getId(),
                publishedAt,
                validUntil,
                List.of("Yellow")
        );
        assertThat(poll).isNotNull();
        assertThat(poll.getId()).isNotNull();

        // Add options explicitly
        VoteOption red   = pollService.addVoteOption(poll.getId(), "Red");
        VoteOption green = pollService.addVoteOption(poll.getId(), "Green");
        assertThat(red).isNotNull();
        assertThat(green).isNotNull();

        // List polls
        List<Poll> polls = pollService.list();
        assertThat(polls).extracting(Poll::getId).contains(poll.getId());

        // User 2 votes on the poll
        Vote firstVote = pollService.castVote(u2.getId(), poll.getId(), red.getId());
        assertThat(firstVote).isNotNull();
        assertThat(firstVote.getVoter().getId()).isEqualTo(u2.getId());
        assertThat(firstVote.getOption().getId()).isEqualTo(red.getId());

        // List votes
        List<Vote> votesNow1 = voteService.list();
        assertThat(votesNow1).hasSize(1);
        assertThat(votesNow1.getFirst().getVoter().getId()).isEqualTo(u2.getId());
        assertThat(votesNow1.getFirst().getOption().getId()).isEqualTo(red.getId());

        // User 2 changes his vote
        Vote updatedVote = voteService.update(firstVote.getId(), u2.getId(), green.getId());
        assertThat(updatedVote).isNotNull();
        assertThat(updatedVote.getId()).isEqualTo(firstVote.getId());
        assertThat(updatedVote.getOption().getId()).isEqualTo(green.getId());

        // List votes
        List<Vote> votesNow2 = voteService.list();
        assertThat(votesNow2).hasSize(1);
        assertThat(votesNow2.getFirst().getVoter().getId()).isEqualTo(u2.getId());
        assertThat(votesNow2.getFirst().getOption().getId()).isEqualTo(green.getId());

        // Try aggregated results
        assertThat(pollService.getAggregatedResults(poll.getId()));

        // Delete the one poll
        pollService.delete(poll.getId());

        // List votes
        List<Vote> votesAfterDelete = voteService.list();
        assertThat(votesAfterDelete).isEmpty();
    }
}
