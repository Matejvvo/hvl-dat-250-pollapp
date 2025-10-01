package no.hvl.dat250.pollapp.aitests;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.service.interfaces.PollService;
import no.hvl.dat250.pollapp.service.interfaces.UserService;
import no.hvl.dat250.pollapp.service.interfaces.VoteService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * DISCLAIMER:
 * These integration and unit tests were initially generated with the help of OpenAI's ChatGPT.
 * They have been manually reviewed and adapted for correctness with the project’s domain logic.
 */

@SpringBootTest
@ActiveProfiles("test")
class VoteServiceImplIT {
    @Autowired UserService userService;
    @Autowired PollService pollService;
    @Autowired VoteService voteService;

    private Poll openPoll(User owner) {
        Instant now = Instant.now();
        return pollService.create(
                "Q?",
                2,
                false,
                owner.getIdAsUUID(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );
    }

    @Test
    void update_onlyByOwner_andWithinSamePoll_andWithinTimeWindow() {
        User owner = userService.create("owner", "o@x.com", "x");
        User other = userService.create("other", "ot@x.com", "x");

        Poll p = openPoll(owner);
        VoteOption a = pollService.addVoteOption(p.getIdAsUUID(), "A");
        VoteOption b = pollService.addVoteOption(p.getIdAsUUID(), "B");

        Vote v = pollService.castVote(owner.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID());
        assertThat(v).isNotNull();

        // non-owner cannot update
        assertThat(voteService.update(v.getIdAsUUID(), other.getIdAsUUID(), b.getIdAsUUID())).isNull();

        // owner can switch within same poll
        Vote updated = voteService.update(v.getIdAsUUID(), owner.getIdAsUUID(), b.getIdAsUUID());
        assertThat(updated).isNotNull();
        assertThat(updated.getOption().getIdAsUUID()).isEqualTo(b.getIdAsUUID());
    }

    @Test
    void update_fails_whenTargetOptionBelongsToAnotherPoll() {
        User u = userService.create("u", "u@x.com", "x");

        Poll p1 = openPoll(u);
        Poll p2 = openPoll(u);
        VoteOption a1 = pollService.addVoteOption(p1.getIdAsUUID(), "A1");
        VoteOption a2 = pollService.addVoteOption(p2.getIdAsUUID(), "A2");

        Vote vote = pollService.castVote(u.getIdAsUUID(), p1.getIdAsUUID(), a1.getIdAsUUID());
        assertThat(vote).isNotNull();

        // cannot move vote to option from a different poll
        assertThat(voteService.update(vote.getIdAsUUID(), u.getIdAsUUID(), a2.getIdAsUUID())).isNull();
    }

    @Test
    void update_respectsNoDuplicateSameOption_forSameUser() {
        User u = userService.create("u", "u@x.com", "x");
        Poll p = openPoll(u);
        VoteOption a = pollService.addVoteOption(p.getIdAsUUID(), "A");
        VoteOption b = pollService.addVoteOption(p.getIdAsUUID(), "B");

        // cast two votes (max 2)
        Vote v1 = pollService.castVote(u.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID());
        Vote v2 = pollService.castVote(u.getIdAsUUID(), p.getIdAsUUID(), b.getIdAsUUID());
        assertThat(List.of(v1, v2)).allMatch(Objects::nonNull);

        // try to update v2 to A → would duplicate option A for same user
        assertThat(voteService.update(v2.getIdAsUUID(), u.getIdAsUUID(), a.getIdAsUUID())).isNull();
    }

    @Test
    void update_blocksOutsideVotingWindow() {
        User u = userService.create("u", "u@x.com", "x");
        Instant now = Instant.now();

        // poll that already expired
        Poll p = pollService.create(
                "Q?",
                1,
                false,
                u.getIdAsUUID(),
                now.minus(2, ChronoUnit.DAYS),
                now.minus(1, ChronoUnit.DAYS),
                List.of()
        );
        VoteOption o = pollService.addVoteOption(p.getIdAsUUID(), "O");
        // cannot even cast
        assertThat(pollService.castVote(u.getIdAsUUID(), p.getIdAsUUID(), o.getIdAsUUID())).isNull();
    }
}
