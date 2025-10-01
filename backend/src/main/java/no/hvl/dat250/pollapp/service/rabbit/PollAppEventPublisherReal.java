package no.hvl.dat250.pollapp.service.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PollAppEventPublisherReal implements PollAppEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PollAppEventPublisherReal(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishPollCreated(PollEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll.created", dto);
    }

    @Override
    public void publishVoteCast(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.cast", dto);
    }

    @Override
    public void publishVoteRemove(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.removed", dto);
    }

    @Override
    public void publishVoteUpdated(VoteEventDTO dto) {
        rabbitTemplate.convertAndSend("polls.x", "poll." + dto.getPollId() + ".vote.updated", dto);
    }
}
