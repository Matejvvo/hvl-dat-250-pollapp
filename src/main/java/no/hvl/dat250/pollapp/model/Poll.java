package no.hvl.dat250.pollapp.model;

import java.time.Instant;
import java.util.*;

public class Poll {
    // --- Attributes ---
    private UUID id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private int maxVotesPerUser;
    private boolean isPrivate;
    private Set<User> allowedVoters;

    // --- Associations ---
    private User creator;
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

    public int getMaxVotesPerUser() {
        return maxVotesPerUser;
    }

    public void setMaxVotesPerUser(int maxVotesPerUser) {
        this.maxVotesPerUser = maxVotesPerUser;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Set<User> getAllowedVoters() {
        return allowedVoters;
    }

    public void setAllowedVoters(Set<User> allowedVoters) {
        this.allowedVoters = allowedVoters;
    }

    // --- Overrides ---
    @Override
    public String toString() {
        return "Poll{" + "id=" + id + ", question='" + question + '\''
                + ", publishedAt=" + publishedAt + ", validUntil=" + validUntil
                + ", visibility=" + isPrivate + ", maxVotesPerUser=" + maxVotesPerUser
                + ", createdBy=" + (creator != null ? creator.getUsername() : "null")
                + ", options=" + options.size() + ", allowedVoters=" + allowedVoters + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Poll that = (Poll) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
