package no.hvl.dat250.redis;

import no.hvl.dat250.pollapp.service.rabbit.PollAppEventPublisher;
import no.hvl.dat250.pollapp.service.rabbit.PollEventDTO;
import no.hvl.dat250.pollapp.service.rabbit.VoteEventDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PollAppEventPublisherNoop implements PollAppEventPublisher {
    @Override public void publishPollCreated(PollEventDTO dto) { }
    @Override public void publishVoteCast(VoteEventDTO dto) { }
    @Override public void publishVoteRemove(VoteEventDTO dto) { }
    @Override public void publishVoteUpdated(VoteEventDTO dto) { }
}
