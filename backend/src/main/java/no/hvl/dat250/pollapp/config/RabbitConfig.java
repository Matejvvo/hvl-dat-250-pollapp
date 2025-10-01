package no.hvl.dat250.pollapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "polls.x";

    @Bean
    public TopicExchange pollsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue pollCreatedQueue() {
        return QueueBuilder.durable("poll.created.q").build();
    }

    @Bean
    public Binding pollCreatedBinding(Queue pollCreatedQueue, TopicExchange pollsExchange) {
        return BindingBuilder.bind(pollCreatedQueue).to(pollsExchange).with("poll.created");
    }

    @Bean
    public Queue votesAllQueue() {
        return QueueBuilder.durable("poll.votes.all.q").build();
    }

    @Bean
    public Binding votesAllBinding(Queue votesAllQueue, TopicExchange pollsExchange) {
        return BindingBuilder.bind(votesAllQueue).to(pollsExchange).with("poll.*.vote.*");
    }

    @Bean
    public Queue voteCommandQueue() {
        return QueueBuilder.durable("vote.cmd.q").build();
    }

    @Bean
    public Binding voteCastCommandBinding(Queue voteCommandQueue, TopicExchange pollsExchange) {
        return BindingBuilder.bind(voteCommandQueue).to(pollsExchange).with("vote.cmd.cast");
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
