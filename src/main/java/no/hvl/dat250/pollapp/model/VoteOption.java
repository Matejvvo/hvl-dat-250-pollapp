package no.hvl.dat250.pollapp.model;

import java.util.UUID;

public class VoteOption {
    // --- Attributes ---
    private UUID id;
    private String caption;
    private int presentationOrder;

    // --- Associations ---
    private Poll poll;

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

    // --- Printer ---
    @Override
    public String toString() {
        return "VoteOption{" + "id=" + id + ", caption='" + caption + '\'' + ", presentationOrder=" + presentationOrder + ", pollId=" + (poll != null ? poll.getId() : "null") + '}';
    }
}
