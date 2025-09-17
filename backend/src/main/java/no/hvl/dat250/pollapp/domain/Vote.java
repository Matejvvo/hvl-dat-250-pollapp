package no.hvl.dat250.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Vote {
    // --- Attributes ---
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant publishedAt;

    // --- Associations ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "vote-user")
    private User voter;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "option-vote")
    private VoteOption option;

    // --- Public Bean Constructor ---
    public Vote() {
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
