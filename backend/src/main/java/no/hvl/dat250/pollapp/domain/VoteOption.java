package no.hvl.dat250.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class VoteOption {
    // --- Attributes ---
    private UUID id;
    private String caption;
    private int presentationOrder;

    // --- Associations ---
    @JsonBackReference(value = "poll-option")
    private Poll poll;
    @JsonManagedReference(value = "option-vote")
    private Set<Vote> votes = new HashSet<>();

    // --- Public Bean Constructor ---
    public VoteOption() {
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPresentationOrder() {
        return presentationOrder;
    }

    public void setPresentationOrder(int presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
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
        return "VoteOption{"    + "caption='" + caption + '\''
                                + ", presentationOrder=" + presentationOrder
                                + ", poll=" + (poll != null ? poll.getQuestion() : "null")
                                + ", votes=" + (votes != null ? votes.size() : 0)
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        VoteOption that = (VoteOption) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
