package no.hvl.dat250.pollapp.service.rabbit;

import no.hvl.dat250.pollapp.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PollAppEventPublisherReal implements PollAppEventPublisher {
    private final RabbitConfig.EventBus eventBus;

    public PollAppEventPublisherReal(RabbitConfig.EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publishPollCreated(PollEventDTO dto) {
        eventBus.send("poll.created", dto);
    }

    @Override
    public void publishVoteCast(VoteEventDTO dto) {
        eventBus.send("poll." + dto.getPollId() + ".vote.cast", dto);
    }

    @Override
    public void publishVoteRemove(VoteEventDTO dto) {
        eventBus.send("poll." + dto.getPollId() + ".vote.removed", dto);
    }

    @Override
    public void publishVoteUpdated(VoteEventDTO dto) {
        eventBus.send("poll." + dto.getPollId() + ".vote.updated", dto);
    }
}
