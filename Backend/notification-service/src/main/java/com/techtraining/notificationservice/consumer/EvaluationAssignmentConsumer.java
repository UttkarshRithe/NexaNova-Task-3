package com.techtraining.notificationservice.consumer;

import com.techtraining.common.event.EvaluationAssignedEvent;
import com.techtraining.notificationservice.config.RabbitMQConfig;
import com.techtraining.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationAssignmentConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeEvaluationAssignedEvent(EvaluationAssignedEvent event) {
        log.info("[EVENT-CONSUMED] Received EvaluationAssignedEvent for assignmentId: {}", event.getAssignmentId());
        try {
            emailService.sendAssignmentEmail(event);
            log.info("[NOTIFICATION-SUCCESS] Email sent successfully for assignmentId: {}", event.getAssignmentId());
        } catch (Exception e) {
            log.error("[NOTIFICATION-FAILED] Failed to send email for assignmentId: {}. Error: {}", 
                    event.getAssignmentId(), e.getMessage());
            // Retry logic is handled by RabbitMQ listener retry configuration in application.yml
            throw e; 
        }
    }
}
