package no.hvl.dat250.pollapp.service.rabbit;

import jakarta.persistence.*;
import no.hvl.dat250.pollapp.domain.Vote;

import java.time.Instant;
import java.util.Objects;

public class VoteEventDTO {
    private String id;
    private Instant publishedAt;
    private String voterId;
    private String optionId;
    private String pollId;

    public VoteEventDTO() {
    }

    public VoteEventDTO(Vote vote) {
        this.id = vote.getId();
        this.publishedAt = vote.getPublishedAt();
        this.voterId = vote.getVoter().getId();
        this.optionId = vote.getOption().getId();
        this.pollId = vote.getOption().getPoll().getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteEventDTO that = (VoteEventDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "VoteEventDTO{" +
                "id='" + id + '\'' +
                ", publishedAt=" + publishedAt +
                ", voterId='" + voterId + '\'' +
                ", optionId='" + optionId + '\'' +
                ", pollId='" + pollId + '\'' +
                '}';
    }
}
