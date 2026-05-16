package com.techtraining.evaluationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "evaltrack.exchange";
    public static final String QUEUE = "evaluation.assignment.notification.queue";
    public static final String ROUTING_KEY = "evaluation.assigned";
    public static final String DLQ = "evaluation.assignment.notification.dlq";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "evaluation.dlq")
                .build();
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(exchange()).with("evaluation.dlq");
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
