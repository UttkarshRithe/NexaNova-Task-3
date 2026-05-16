package com.techtraining.reportservice.producer;

import com.techtraining.common.event.ParticipantReportEmailEvent;
import com.techtraining.reportservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantReportEmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishParticipantReportEmailEvent(ParticipantReportEmailEvent event) {
        log.info("[EVENT-PUBLISHING] Publishing ParticipantReportEmailEvent for participantId: {}", event.getParticipantId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event
            );
            log.info("[EVENT-PUBLISHED] Participant report email event published successfully for participantId: {}", event.getParticipantId());
        } catch (Exception e) {
            log.error("[EVENT-FAILURE] Failed to publish participant report email event for participantId: {}. Error: {}", 
                      event.getParticipantId(), e.getMessage());
        }
    }
}
