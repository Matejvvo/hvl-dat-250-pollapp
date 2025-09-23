package no.hvl.dat250.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@RedisHash("votes")
public class Vote {
    // --- Attributes ---
    @Id
    private String id;
    private Instant publishedAt;

    // --- Associations ---
    @JsonBackReference(value = "vote-user")
    @Reference
    private User voter;
    @JsonBackReference(value = "option-vote")
    @Reference
    private VoteOption option;

    // --- Public Bean Constructor ---
    public Vote() {
    }

    // --- Getters & Setters ---
    public UUID getIdAsUUID() {
        return UUID.fromString(id);
    }

    public String getId() {
        return this.id;
    }

    public void setId(UUID uuid) {
        this.id = uuid.toString();
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public VoteOption getOption() {
        return option;
    }

    public void setOption(VoteOption option) {
        this.option = option;
    }

    // --- Overrides ---
    @Override
    public String toString() {
        return "Vote{"  + "publishedAt=" + publishedAt
                        + ", voter=" + (voter != null ? voter.getUsername() : "null")
                        + ", option=" + (option != null ? option.getCaption() : "null")
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Vote that = (Vote) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
