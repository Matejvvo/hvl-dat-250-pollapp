package no.hvl.dat250.pollapp.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Poll {
    // --- Attributes ---
    private UUID id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private Visibility visibility;
    private int maxVotesPerUser;

    // --- Associations ---
    private User createdBy;
    private List<VoteOption> options = new ArrayList<>();

    // --- Public Bean Constructor ---
    public Poll() {
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public int getMaxVotesPerUser() {
        return maxVotesPerUser;
    }

    public void setMaxVotesPerUser(int maxVotesPerUser) {
        this.maxVotesPerUser = maxVotesPerUser;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options;
    }

    // --- Printer ---
    @Override
    public String toString() {
        return "Poll{" + "id=" + id + ", question='" + question + '\'' + ", publishedAt=" + publishedAt + ", validUntil=" + validUntil + ", visibility=" + visibility + ", maxVotesPerUser=" + maxVotesPerUser + ", createdBy=" + (createdBy != null ? createdBy.getUsername() : "null") + ", options=" + options.size() + '}';
    }

    // --- Helper Enum Methods ---
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }

    public boolean isPrivate() {
        return visibility == Visibility.PRIVATE;
    }

    // --- Helper Enum ---
    public enum Visibility {
        PUBLIC, PRIVATE,
    }
}
