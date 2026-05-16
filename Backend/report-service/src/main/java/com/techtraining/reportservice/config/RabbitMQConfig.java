package com.techtraining.reportservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "evaltrack.exchange";
    public static final String QUEUE = "participant.report.email.queue";
    public static final String ROUTING_KEY = "participant.report.email";
    public static final String DLQ = "participant.report.email.dlq";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue reportQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue reportDlq() {
        return new Queue(DLQ);
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding reportDlqBinding() {
        return BindingBuilder.bind(reportDlq()).to(exchange()).with(DLQ);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
