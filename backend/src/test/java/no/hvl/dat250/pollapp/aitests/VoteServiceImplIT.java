package no.hvl.dat250.pollapp.aitests;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.User;
import no.hvl.dat250.pollapp.domain.Vote;
import no.hvl.dat250.pollapp.domain.VoteOption;
import no.hvl.dat250.pollapp.service.PollService;
import no.hvl.dat250.pollapp.service.UserService;
import no.hvl.dat250.pollapp.service.VoteService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                owner.getId(),
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
        VoteOption a = pollService.addVoteOption(p.getId(), "A");
        VoteOption b = pollService.addVoteOption(p.getId(), "B");

        Vote v = pollService.castVote(owner.getId(), p.getId(), a.getId());
        assertThat(v).isNotNull();

        // non-owner cannot update
        assertThat(voteService.update(v.getId(), other.getId(), b.getId())).isNull();

        // owner can switch within same poll
        Vote updated = voteService.update(v.getId(), owner.getId(), b.getId());
        assertThat(updated).isNotNull();
        assertThat(updated.getOption().getId()).isEqualTo(b.getId());
    }

    @Test
    void update_fails_whenTargetOptionBelongsToAnotherPoll() {
        User u = userService.create("u", "u@x.com", "x");

        Poll p1 = openPoll(u);
        Poll p2 = openPoll(u);
        VoteOption a1 = pollService.addVoteOption(p1.getId(), "A1");
        VoteOption a2 = pollService.addVoteOption(p2.getId(), "A2");

        Vote vote = pollService.castVote(u.getId(), p1.getId(), a1.getId());
        assertThat(vote).isNotNull();

        // cannot move vote to option from a different poll
        assertThat(voteService.update(vote.getId(), u.getId(), a2.getId())).isNull();
    }

    @Test
    void update_respectsNoDuplicateSameOption_forSameUser() {
        User u = userService.create("u", "u@x.com", "x");
        Poll p = openPoll(u);
        VoteOption a = pollService.addVoteOption(p.getId(), "A");
        VoteOption b = pollService.addVoteOption(p.getId(), "B");

        // cast two votes (max 2)
        Vote v1 = pollService.castVote(u.getId(), p.getId(), a.getId());
        Vote v2 = pollService.castVote(u.getId(), p.getId(), b.getId());
        assertThat(List.of(v1, v2)).allMatch(Objects::nonNull);

        // try to update v2 to A → would duplicate option A for same user
        assertThat(voteService.update(v2.getId(), u.getId(), a.getId())).isNull();
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
                u.getId(),
                now.minus(2, ChronoUnit.DAYS),
                now.minus(1, ChronoUnit.DAYS),
                List.of()
        );
        VoteOption o = pollService.addVoteOption(p.getId(), "O");
        // cannot even cast
        assertThat(pollService.castVote(u.getId(), p.getId(), o.getId())).isNull();
    }
}
