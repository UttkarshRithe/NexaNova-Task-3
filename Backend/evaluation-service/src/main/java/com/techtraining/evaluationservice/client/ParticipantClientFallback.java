package com.techtraining.evaluationservice.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class ParticipantClientFallback implements ParticipantClient {
    @Override
    public Map<String, Object> getEnrollmentById(Long id) {
        return new HashMap<>();
    }
    @Override
    public Map<String, Object> getParticipantById(Long id) {
        return new HashMap<>();
    }
    @Override
    public String getParticipantEmailByEnrollmentId(Long id) {
        return "fallback@nexanova.ai";
    }
}
