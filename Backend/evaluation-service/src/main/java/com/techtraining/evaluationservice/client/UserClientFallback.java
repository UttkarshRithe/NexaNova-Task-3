package com.techtraining.evaluationservice.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public Map<String, Object> getUserById(Long id) {
        return new HashMap<>();
    }
}
