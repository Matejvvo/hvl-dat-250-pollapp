package no.hvl.dat250.pollapp.service.rabbit;

import no.hvl.dat250.pollapp.domain.Vote;

import java.time.Instant;
import java.util.Objects;

public class VoteEventDTO {
    private final String id;
    private final Instant publishedAt;
    private final String voterId;
    private final String optionId;
    private final String pollId;

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

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getPollId() {
        return pollId;
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
                "id='" + this.getId() + '\'' +
                ", publishedAt=" + this.getPublishedAt() +
                ", voterId='" + this.getVoterId() + '\'' +
                ", optionId='" + this.getOptionId() + '\'' +
                ", pollId='" + this.getPollId() + '\'' +
                '}';
    }
}
