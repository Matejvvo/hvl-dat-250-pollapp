package no.hvl.dat250.pollapp.service.rabbit;

import no.hvl.dat250.pollapp.domain.Poll;
import no.hvl.dat250.pollapp.domain.VoteOption;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PollEventDTO {
    private String id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private int maxVotesPerUser;
    private boolean isPrivate;
    private String creatorId;
    private List<String> optionIds = new ArrayList<>();

    public PollEventDTO() {
    }

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

    public void setId(String id) {
        this.id = id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getOptionIds() {
        return optionIds;
    }

    public void setOptionIds(List<String> optionIds) {
        this.optionIds = optionIds;
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
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", publishedAt=" + publishedAt +
                ", validUntil=" + validUntil +
                ", maxVotesPerUser=" + maxVotesPerUser +
                ", isPrivate=" + isPrivate +
                ", creatorId='" + creatorId + '\'' +
                ", optionIds=" + optionIds +
                '}';
    }
}
