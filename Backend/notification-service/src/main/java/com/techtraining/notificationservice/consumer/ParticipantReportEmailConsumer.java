package com.techtraining.notificationservice.consumer;

import com.techtraining.common.event.ParticipantReportEmailEvent;
import com.techtraining.notificationservice.config.RabbitMQConfig;
import com.techtraining.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParticipantReportEmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.REPORT_QUEUE)
    public void consumeParticipantReportEmailEvent(ParticipantReportEmailEvent event) {
        log.info("[EVENT-RECEIVED] Received ParticipantReportEmailEvent for participant: {}", event.getParticipantName());
        try {
            emailService.sendParticipantReport(event);
            log.info("[EVENT-PROCESSED] Successfully processed ParticipantReportEmailEvent for: {}", event.getParticipantName());
        } catch (Exception e) {
            log.error("[EVENT-ERROR] Failed to process ParticipantReportEmailEvent for: {}. Error: {}", 
                      event.getParticipantName(), e.getMessage());
            throw e; // Rethrow to trigger RabbitMQ retry
        }
    }
}
