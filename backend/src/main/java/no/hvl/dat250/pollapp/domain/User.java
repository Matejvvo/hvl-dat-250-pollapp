package no.hvl.dat250.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RedisHash("users")
@Entity
@Table(name = "users")
public class User {
    // --- Attributes --
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String username;
    private String email;

    // Associations
    @JsonManagedReference(value = "poll-user")
    @Reference
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Poll> polls = new HashSet<>();
    @JsonManagedReference(value = "vote-user")
    @Reference
    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Vote> votes = new HashSet<>();

    // --- Public Bean Constructor ---
    public User() {
    }

    // --- Getters & Setters ---
    public UUID getIdAsUUID() {
        if (id == null) return null;
        return UUID.fromString(id);
    }

    public String getId() {
        return this.id;
    }

    public void setId(UUID uuid) {
        this.id = uuid.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Poll> getPolls() {
        return polls;
    }

    public void setPolls(Set<Poll> polls) {
        this.polls = polls;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    // --- Overrides ---
    @Override
    public String toString() {
        return "User{"  + "username='" + username
                        + ", email='" + email
                        + ", createdPolls=" + polls.size()
                        + ", votes=" + votes.size()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // JPA
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Poll createPoll(String question) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setCreator(this);
        poll.setMaxVotesPerUser(1);
        poll.setIsPrivate(false);
        poll.setPublishedAt(Instant.now());
        poll.setValidUntil(Instant.now().plus(10, ChronoUnit.DAYS));
        polls.add(poll);
        return poll;
    }

    public Vote voteFor(VoteOption option) {
        Vote vote = new Vote();
        vote.setOption(option);
        vote.setVoter(this);
        vote.setPublishedAt(Instant.now());
        votes.add(vote);
        return vote;
    }
}
