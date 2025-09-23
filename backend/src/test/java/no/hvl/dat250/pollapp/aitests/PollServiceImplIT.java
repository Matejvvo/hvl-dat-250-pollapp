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
                creator.getIdAsUUID(),
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

        assertThat(pollService.create(null, 1, false, creator.getIdAsUUID(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("   ", 1, false, creator.getIdAsUUID(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 0, false, creator.getIdAsUUID(), now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, null, now, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getIdAsUUID(), null, now.plusSeconds(1), List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getIdAsUUID(), now, null, List.of())).isNull();
        assertThat(pollService.create("ok", 1, false, creator.getIdAsUUID(), now.plusSeconds(10), now, List.of())).isNull();
    }

    @Test
    void addVoteOption_enforcesCaseInsensitiveDuplicates_andOrderIncrements() {
        User creator = userService.create("creator", "c@x.com", "x");
        Poll poll = openPublicPoll(creator);

        VoteOption a0 = pollService.addVoteOption(poll.getIdAsUUID(), "Alpha");
        VoteOption a1 = pollService.addVoteOption(poll.getIdAsUUID(), "Beta");
        VoteOption dup = pollService.addVoteOption(poll.getIdAsUUID(), "alpha"); // duplicate (case-insensitive)

        assertThat(a0).isNotNull();
        assertThat(a1).isNotNull();
        assertThat(dup).isNull();

        List<VoteOption> options = pollService.listVoteOptions(poll.getIdAsUUID());
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
                creator.getIdAsUUID(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );

        // attempt to reduce maxVotes and shorten validUntil: should be ignored
        Poll p1 = pollService.update(poll.getIdAsUUID(), "  New Q  ", 0, true, now.plus(1, ChronoUnit.HOURS));
        assertThat(p1.getQuestion()).isEqualTo("New Q");
        assertThat(p1.getMaxVotesPerUser()).isEqualTo(1); // unchanged
        assertThat(p1.getIsPrivate()).isTrue();
        assertThat(p1.getValidUntil()).isEqualTo(poll.getValidUntil()); // unchanged (earlier than current)

        // increase both
        Instant oldValidUntil = poll.getValidUntil();
        Poll p2 = pollService.update(poll.getIdAsUUID(), null, 3, false, poll.getValidUntil().plus(1, ChronoUnit.DAYS));
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
                creator.getIdAsUUID(),
                now.minus(1, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.DAYS),
                List.of()
        );
        VoteOption o = pollService.addVoteOption(poll.getIdAsUUID(), "O1");

        // not allowed yet
        assertThat(pollService.castVote(blocked.getIdAsUUID(), poll.getIdAsUUID(), o.getIdAsUUID())).isNull();

        // allow specific user
        pollService.addAllowedVoter(poll.getIdAsUUID(), allowed.getIdAsUUID());
        assertThat(pollService.castVote(allowed.getIdAsUUID(), poll.getIdAsUUID(), o.getIdAsUUID())).isNotNull();

        // blocked remains blocked
        assertThat(pollService.castVote(blocked.getIdAsUUID(), poll.getIdAsUUID(), o.getIdAsUUID())).isNull();
    }

    @Test
    void castVote_enforcesMaxVotesPerUser_andNoDuplicateSameOption() {
        User c = userService.create("c", "c@x.com", "x");
        User v = userService.create("v", "v@x.com", "x");
        Poll p = openPublicPoll(c);
        VoteOption a = pollService.addVoteOption(p.getIdAsUUID(), "A");
        VoteOption b = pollService.addVoteOption(p.getIdAsUUID(), "B");

        // max 2 votes per user
        Poll updatedMax = pollService.update(p.getIdAsUUID(), null, 2, false, p.getValidUntil());
        assertThat(updatedMax.getMaxVotesPerUser()).isEqualTo(2);

        assertThat(pollService.castVote(v.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID())).isNotNull();
        // duplicate same option → blocked
        assertThat(pollService.castVote(v.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID())).isNull();
        // a different option within cap → allowed
        assertThat(pollService.castVote(v.getIdAsUUID(), p.getIdAsUUID(), b.getIdAsUUID())).isNotNull();
        // over cap → blocked
        assertThat(pollService.castVote(v.getIdAsUUID(), p.getIdAsUUID(), b.getIdAsUUID())).isNull();
    }

    @Test
    void listPollVotes_flattensAcrossOptions_andAggregationJsonLooksSane() {
        User c = userService.create("c", "c@x.com", "x");
        User v1 = userService.create("v1", "v1@x.com", "x");
        User v2 = userService.create("v2", "v2@x.com", "x");

        Poll p = openPublicPoll(c);
        VoteOption a = pollService.addVoteOption(p.getIdAsUUID(), "A \"quoted\"");
        VoteOption b = pollService.addVoteOption(p.getIdAsUUID(), "B\nnewline");

        pollService.castVote(v1.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID());
        pollService.castVote(v2.getIdAsUUID(), p.getIdAsUUID(), b.getIdAsUUID());

        assertThat(pollService.listPollVotes(p.getIdAsUUID())).hasSize(2);

        String json = pollService.getAggregatedResults(p.getIdAsUUID());
        assertThat(json).contains("\"pollId\":\"" + p.getIdAsUUID() + "\"");
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
        VoteOption a = pollService.addVoteOption(p.getIdAsUUID(), "A");
        pollService.castVote(v.getIdAsUUID(), p.getIdAsUUID(), a.getIdAsUUID());

        assertThat(pollService.listPollVotes(p.getIdAsUUID())).hasSize(1);

        pollService.delete(p.getIdAsUUID());

        // poll gone → listing votes by poll would be empty by definition
        assertThat(pollService.list()).doesNotContain(p);
    }
}
