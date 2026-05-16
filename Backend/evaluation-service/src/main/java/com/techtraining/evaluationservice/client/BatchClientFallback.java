package com.techtraining.evaluationservice.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class BatchClientFallback implements BatchClient {
    @Override
    public Map<String, Object> getBatchTechnologyById(Long id) {
        return new HashMap<>();
    }
}
