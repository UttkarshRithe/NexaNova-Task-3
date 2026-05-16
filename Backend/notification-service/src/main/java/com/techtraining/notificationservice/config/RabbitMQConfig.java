package com.techtraining.notificationservice.config;

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

    public static final String REPORT_QUEUE = "participant.report.email.queue";
    public static final String REPORT_ROUTING_KEY = "participant.report.email";
    public static final String REPORT_DLQ = "participant.report.email.dlq";

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
    public Queue reportQueue() {
        return QueueBuilder.durable(REPORT_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", REPORT_DLQ)
                .build();
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ);
    }

    @Bean
    public Queue reportDlq() {
        return new Queue(REPORT_DLQ);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue()).to(exchange()).with(REPORT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
