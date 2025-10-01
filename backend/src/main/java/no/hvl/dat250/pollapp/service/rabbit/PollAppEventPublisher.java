package no.hvl.dat250.pollapp.service.rabbit;

public interface PollAppEventPublisher {
    void publishPollCreated(PollEventDTO dto);
    void publishVoteCast(VoteEventDTO dto);
    void publishVoteRemove(VoteEventDTO dto);
    void publishVoteUpdated(VoteEventDTO dto);
}
