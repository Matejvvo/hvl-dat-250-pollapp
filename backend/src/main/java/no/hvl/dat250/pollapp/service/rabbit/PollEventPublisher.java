package no.hvl.dat250.pollapp.service.rabbit;

import no.hvl.dat250.pollapp.domain.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PollEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PollEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPollCreated(PollEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll.created", dto);
    }

    public void publishVoteCast(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.cast", dto);
    }

    public void publishVoteRemove(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.removed", dto);
    }

    public void publishVoteUpdated(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.updated", dto);
    }
}
