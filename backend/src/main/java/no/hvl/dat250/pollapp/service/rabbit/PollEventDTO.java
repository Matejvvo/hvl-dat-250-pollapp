package no.hvl.dat250.pollapp.service.rabbit;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.VoteOption;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PollEventDTO {
    private final String id;
    private final String question;
    private final Instant publishedAt;
    private final Instant validUntil;
    private final int maxVotesPerUser;
    private final boolean isPrivate;
    private final String creatorId;
    private final List<String> optionIds = new ArrayList<>();

    public PollEventDTO(Poll poll) {
        this.id = poll.getId();
        this.question = poll.getQuestion();
        this.publishedAt = poll.getPublishedAt();
        this.validUntil = poll.getValidUntil();
        this.maxVotesPerUser = poll.getMaxVotesPerUser();
        this.isPrivate = poll.getIsPrivate();
        this.creatorId = poll.getCreator().getId();
        for (VoteOption voteOption : poll.getOptions())
            this.optionIds.add(voteOption.getId());
    }

    public String getId() {
        return id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getQuestion() {
        return question;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public int getMaxVotesPerUser() {
        return maxVotesPerUser;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public List<String> getOptionIds() {
        return optionIds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PollEventDTO that = (PollEventDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PollEventDTO{" +
                "id='" + this.getId() + '\'' +
                ", question='" + this.getQuestion() + '\'' +
                ", publishedAt=" + this.getPublishedAt() +
                ", validUntil=" + this.getValidUntil() +
                ", maxVotesPerUser=" + this.getMaxVotesPerUser() +
                ", isPrivate=" + this.getIsPrivate() +
                ", creatorId='" + this.getCreatorId() + '\'' +
                ", optionIds=" + this.getOptionIds() +
                '}';
    }
}
