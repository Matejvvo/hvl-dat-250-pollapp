package no.hvl.dat250.pollapp.service.rabbit;

import jakarta.transaction.Transactional;
import no.hvl.dat250.pollapp.service.interfaces.PollService;
import no.hvl.dat250.pollapp.service.interfaces.VoteService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VoteEventsListener {
    private final PollService pollService;
    private final VoteService voteService;

    public VoteEventsListener(PollService pollService, VoteService voteService) {
        this.pollService = pollService;
        this.voteService = voteService;
    }

    @Transactional
    @RabbitListener(queues = "vote.cmd.q")
    public void onVoteEvent(VoteEventDTO dto, @Header(name = AmqpHeaders.RECEIVED_ROUTING_KEY, required = false) String routingKey) {
        if (dto == null) return;
        if (dto.getPollId() == null || dto.getVoterId() == null || dto.getOptionId() == null) return;

        System.out.println("Got RabbitMQ message");
        System.out.println(routingKey);

        UUID voteId = UUID.fromString(dto.getVoterId());
        UUID voterId = UUID.fromString(dto.getVoterId());
        UUID pollId = UUID.fromString(dto.getPollId());
        UUID optionId = UUID.fromString(dto.getOptionId());

        String command = commandFromRoutingKey(routingKey);
        switch (command) {
            case "cast"    -> pollService.castVote(voterId, pollId, optionId);
            case "update"  -> voteService.update(voteId, voterId, optionId);
            case "remove"  -> voteService.delete(voteId);
            default -> System.out.println("Unknown command: " + command);
        }
    }

    static private String commandFromRoutingKey(String rk) {
        if (rk == null) return "";
        int lastDot = rk.lastIndexOf('.');
        return lastDot >= 0 ? rk.substring(lastDot + 1) : rk;
    }
}
