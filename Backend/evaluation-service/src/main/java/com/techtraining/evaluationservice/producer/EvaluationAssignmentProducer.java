package com.techtraining.evaluationservice.producer;

import com.techtraining.common.event.EvaluationAssignedEvent;
import com.techtraining.evaluationservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationAssignmentProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishEvaluationAssignedEvent(EvaluationAssignedEvent event) {
        try {
            log.info("[EVENT-PUBLISHING] Publishing EvaluationAssignedEvent for assignmentId: {}", event.getAssignmentId());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
            log.info("[EVENT-PUBLISHED] EvaluationAssignedEvent published successfully for assignmentId: {}", event.getAssignmentId());
        } catch (Exception e) {
            log.error("[EVENT-PUBLISH-FAILED] Failed to publish EvaluationAssignedEvent for assignmentId: {}. Error: {}", 
                    event.getAssignmentId(), e.getMessage());
        }
    }
}
