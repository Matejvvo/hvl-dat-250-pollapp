package no.hvl.dat250.pollapp.aitests;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.service.PollService;
import no.hvl.dat250.pollapp.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * DISCLAIMER:
 * These integration and unit tests were initially generated with the help of OpenAI's ChatGPT.
 * They have been manually reviewed and adapted for correctness with the project’s domain logic.
 */

@SpringBootTest
@Transactional
class UserServiceImplIT {

    @Autowired UserService userService;
    @Autowired PollService pollService;

    @Test
    void create_and_list_and_get_and_update() {
        User u = userService.create("  alice  ", "  alice@example.com  ", "ignored");
        assertThat(u).isNotNull();
        assertThat(u.getUsername()).isEqualTo("alice");
        assertThat(u.getEmail()).isEqualTo("alice@example.com");

        assertThat(userService.list()).extracting(User::getId).contains(u.getId());
        assertThat(userService.get(u.getId()).getUsername()).isEqualTo("alice");

        User upd = userService.update(u.getId(), "  alice2 ", "  a2@example.com ");
        assertThat(upd.getUsername()).isEqualTo("alice2");
        assertThat(upd.getEmail()).isEqualTo("a2@example.com");
    }

    @Test
    void delete_user_removesTheirPollsAndVotes() {
        User owner = userService.create("owner", "o@x.com", "x");
        User voter = userService.create("voter", "v@x.com", "x");

        Instant now = Instant.now();
        Poll p = pollService.create(
                "Q?",
                1,
                false,
                owner.getId(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );
        VoteOption o = pollService.addVoteOption(p.getId(), "O1");
        pollService.castVote(voter.getId(), p.getId(), o.getId());

        // sanity
        assertThat(userService.listPolls(owner.getId())).extracting(Poll::getId).contains(p.getId());

        // delete owner → their poll is deleted too
        userService.delete(owner.getId());

        // voter’s votes should have been removed by poll delete cascade
        assertThat(userService.listVotes(voter.getId())).isEmpty();
    }
}
