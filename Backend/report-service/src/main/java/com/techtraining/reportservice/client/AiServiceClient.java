package com.techtraining.reportservice.client;

import com.techtraining.common.dto.ApiResponse;
import com.techtraining.reportservice.dto.BatchAnalysisRequest;
import com.techtraining.reportservice.dto.BatchAnalysisResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-service",
        configuration = com.techtraining.reportservice.config.FeignConfig.class,
        fallback = AiServiceClientFallback.class
)
public interface AiServiceClient {

    @PostMapping("/api/ai/generate-batch-analysis")
    ApiResponse<BatchAnalysisResponse> generateBatchAnalysis(@RequestBody BatchAnalysisRequest request);
}

