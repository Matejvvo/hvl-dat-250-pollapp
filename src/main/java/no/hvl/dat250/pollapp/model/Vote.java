package no.hvl.dat250.pollapp.model;

import java.time.Instant;
import java.util.UUID;

public class Vote {
    // --- Attributes ---
    private UUID id;
    private Instant publishedAt;

    // --- Associations ---
    private User voter;
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

    // --- Printer ---
    @Override
    public String toString() {
        return "Vote{" + "id=" + id + ", publishedAt=" + publishedAt + ", voter=" + (voter != null ? voter.getUsername() : "null") + ", option=" + (option != null ? option.getCaption() : "null") + '}';
    }
}
