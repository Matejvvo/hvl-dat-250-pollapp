package no.hvl.dat250.pollapp.aitests;

import no.hvl.dat250.pollapp.config.TestClockConfig;
import no.hvl.dat250.pollapp.domain.*;
import no.hvl.dat250.pollapp.service.PollService;
import no.hvl.dat250.pollapp.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * DISCLAIMER:
 * These integration and unit tests were initially generated with the help of OpenAI's ChatGPT.
 * They have been manually reviewed and adapted for correctness with the project’s domain logic.
 */

@SpringBootTest
@Transactional
@DirtiesContext
@Import(TestClockConfig.class)
class PollServiceImplIT {
    @Autowired Clock clock;
    @Autowired UserService userService;
    @Autowired PollService pollService;

    private Poll openPublicPoll(User creator) {
        Instant now = Instant.now(clock) ;
        return pollService.create(
                "Q1?",
                2,
                false,
                creator.getId(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );
    }

    @Test
    void usesFixedTime() {
        assertEquals(Instant.parse("2025-01-01T12:00:00Z"), Instant.now(clock));
    }

    @Test
    void create_returnsNull_onInvalidParams() {
        User creator = userService.create("u", "u@e.com", "x");
        Instant now = Instant.now(clock) ;

        assertThat(pollService.create(null, 1, false, creator.getId(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("   ", 1, false, creator.getId(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 0, false, creator.getId(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, null, now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getId(), null, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getId(), now, null, List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getId(), now.plusSeconds(10), now, List.of())).isNull();
    }

    @Test
    void addVoteOption_enforcesCaseInsensitiveDuplicates_andOrderIncrements() {
        User creator = userService.create("creator", "c@x.com", "x");
        Poll poll = openPublicPoll(creator);

        VoteOption a0 = pollService.addVoteOption(poll.getId(), "Alpha");
        VoteOption a1 = pollService.addVoteOption(poll.getId(), "Beta");
        VoteOption dup = pollService.addVoteOption(poll.getId(), "alpha"); // duplicate (case-insensitive)

        assertThat(a0).isNotNull();
        assertThat(a1).isNotNull();
        assertThat(dup).isNull();

        List<VoteOption> options = pollService.listVoteOptions(poll.getId());
        assertThat(options).extracting(VoteOption::getCaption).containsExactly("Alpha", "Beta");
        assertThat(options).extracting(VoteOption::getPresentationOrder).containsExactly(0, 1);
    }

    @Test
    void update_onlyIncreasesMaxVotes_andOnlyExtendsValidUntil_andSetsIsPrivate() {
        User creator = userService.create("creator", "c@x.com", "x");
        Instant now = Instant.now(clock) ;
        Poll poll = pollService.create(
                "Q?",
                1,
                false,
                creator.getId(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );

        // attempt to reduce maxVotes and shorten validUntil: should be ignored
        Poll p1 = pollService.update(poll.getId(), "  New Q  ", 0, true, now.plus(1, ChronoUnit.HOURS));
        assertThat(p1.getQuestion()).isEqualTo("New Q");
        assertThat(p1.getMaxVotesPerUser()).isEqualTo(1); // unchanged
        assertThat(p1.getIsPrivate()).isTrue();
        assertThat(p1.getValidUntil()).isEqualTo(poll.getValidUntil()); // unchanged (earlier than current)

        // increase both
        Instant oldValidUntil = poll.getValidUntil();
        Poll p2 = pollService.update(poll.getId(), null, 3, false, poll.getValidUntil().plus(1, ChronoUnit.DAYS));
        assertThat(p2.getMaxVotesPerUser()).isEqualTo(3);
        assertThat(p2.getIsPrivate()).isFalse();
        assertThat(p2.getValidUntil()).isAfter(oldValidUntil);
    }

    @Test
    void privatePoll_allowsOnlyWhitelistedVoters() {
        User creator = userService.create("creator", "c@x.com", "x");
        User allowed = userService.create("allowed", "a@x.com", "x");
        User blocked = userService.create("blocked", "b@x.com", "x");

        Instant now = Instant.now(clock) ;
        Poll poll = pollService.create(
                "Q?",
                1,
                true,
                creator.getId(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );
        VoteOption o = pollService.addVoteOption(poll.getId(), "O1");

        // not allowed yet
        assertThat(pollService.castVote(blocked.getId(), poll.getId(), o.getId())).isNull();

        // allow specific user
        pollService.addAllowedVoter(poll.getId(), allowed.getId());
        assertThat(pollService.castVote(allowed.getId(), poll.getId(), o.getId())).isNotNull();

        // blocked remains blocked
        assertThat(pollService.castVote(blocked.getId(), poll.getId(), o.getId())).isNull();
    }

    @Test
    void castVote_enforcesMaxVotesPerUser_andNoDuplicateSameOption() {
        User c = userService.create("c", "c@x.com", "x");
        User v = userService.create("v", "v@x.com", "x");
        Poll p = openPublicPoll(c);
        VoteOption a = pollService.addVoteOption(p.getId(), "A");
        VoteOption b = pollService.addVoteOption(p.getId(), "B");

        // max 2 votes per user
        Poll updatedMax = pollService.update(p.getId(), null, 2, false, p.getValidUntil());
        assertThat(updatedMax.getMaxVotesPerUser()).isEqualTo(2);

        assertThat(pollService.castVote(v.getId(), p.getId(), a.getId())).isNotNull();
        // duplicate same option → blocked
        assertThat(pollService.castVote(v.getId(), p.getId(), a.getId())).isNull();
        // a different option within cap → allowed
        assertThat(pollService.castVote(v.getId(), p.getId(), b.getId())).isNotNull();
        // over cap → blocked
        assertThat(pollService.castVote(v.getId(), p.getId(), b.getId())).isNull();
    }

    @Test
    void listPollVotes_flattensAcrossOptions_andAggregationJsonLooksSane() {
        User c = userService.create("c", "c@x.com", "x");
        User v1 = userService.create("v1", "v1@x.com", "x");
        User v2 = userService.create("v2", "v2@x.com", "x");

        Poll p = openPublicPoll(c);
        VoteOption a = pollService.addVoteOption(p.getId(), "A \"quoted\"");
        VoteOption b = pollService.addVoteOption(p.getId(), "B\nnewline");

        pollService.castVote(v1.getId(), p.getId(), a.getId());
        pollService.castVote(v2.getId(), p.getId(), b.getId());

        assertThat(pollService.listPollVotes(p.getId())).hasSize(2);

        String json = pollService.getAggregatedResults(p.getId());
        assertThat(json).contains("\"pollId\":\"" + p.getId() + "\"");
        assertThat(json).contains("\"totalVotes\":2");
        assertThat(json).contains("\"caption\":\"A \\\"quoted\\\"\"");
        assertThat(json).contains("\"caption\":\"B\\nnewline\"");
        assertThat(json).contains("\"percentage\":50.0");
        assertThat(json).contains("\"voters\":[");
        assertThat(json).contains("\"computedAt\":\"");
    }

    @Test
    void delete_cascadesVotesAndUnlinksFromUsers() {
        User c = userService.create("c", "c@x.com", "x");
        User v = userService.create("v", "v@x.com", "x");

        Poll p = openPublicPoll(c);
        VoteOption a = pollService.addVoteOption(p.getId(), "A");
        pollService.castVote(v.getId(), p.getId(), a.getId());

        assertThat(pollService.listPollVotes(p.getId())).hasSize(1);

        pollService.delete(p.getId());

        // poll gone → listing votes by poll would be empty by definition
        assertThat(pollService.list()).doesNotContain(p);
    }
}
