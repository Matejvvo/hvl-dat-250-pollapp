package no.hvl.dat250.pollapp;

import no.hvl.dat250.pollapp.model.Poll;
import no.hvl.dat250.pollapp.model.User;
import no.hvl.dat250.pollapp.model.Vote;
import no.hvl.dat250.pollapp.model.VoteOption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PollAppApplicationTests {
    @Autowired private PollManager pollManager;

    @Test
    void testAddAndRetrieveUser() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("password123");
        user.setEmail("alice@example.com");

        User saved = pollManager.addUser(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(pollManager.getUser(saved.getId())).isEqualTo(saved);
    }

    @Test
    void testAddAndRetrievePoll() {
        User user = new User();
        user.setUsername("bob");
        pollManager.addUser(user);

        Poll poll = new Poll();
        poll.setQuestion("Do you like coffee?");
        poll.setCreatedBy(user);
        poll.setPublishedAt(Instant.now());
        poll.setVisibility(Poll.Visibility.PUBLIC);

        Poll saved = pollManager.addPoll(poll);

        assertThat(saved.getId()).isNotNull();
        assertThat(pollManager.getPoll(saved.getId()).getQuestion()).isEqualTo("Do you like coffee?");
    }

    @Test
    void testAddVoteOptionToPoll() {
        Poll poll = new Poll();
        poll.setQuestion("Favorite color?");
        pollManager.addPoll(poll);

        VoteOption option = new VoteOption();
        option.setCaption("Blue");
        option.setPresentationOrder(1);
        option.setPoll(poll);

        VoteOption saved = pollManager.addOption(option);

        assertThat(saved.getId()).isNotNull();
        assertThat(pollManager.getOption(saved.getId()).getCaption()).isEqualTo("Blue");
    }

    @Test
    void testAddVote() {
        User user = new User();
        user.setUsername("charlie");
        pollManager.addUser(user);

        Poll poll = new Poll();
        poll.setQuestion("Tea or Coffee?");
        pollManager.addPoll(poll);

        VoteOption option = new VoteOption();
        option.setCaption("Tea");
        option.setPoll(poll);
        pollManager.addOption(option);

        Vote vote = new Vote();
        vote.setVoter(user);
        vote.setOption(option);
        vote.setPublishedAt(Instant.now());

        Vote saved = pollManager.addVote(vote);

        assertThat(saved.getId()).isNotNull();
        assertThat(pollManager.getVote(saved.getId()).getOption().getCaption()).isEqualTo("Tea");
        assertThat(saved.getVoter().getUsername()).isEqualTo("charlie");
    }
}
