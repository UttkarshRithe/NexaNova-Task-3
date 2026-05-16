package com.techtraining.reportservice.client;

import com.techtraining.common.dto.ApiResponse;
import com.techtraining.reportservice.dto.BatchAnalysisRequest;
import com.techtraining.reportservice.dto.BatchAnalysisResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class AiServiceClientFallback implements AiServiceClient {
    @Override
    public ApiResponse<BatchAnalysisResponse> generateBatchAnalysis(BatchAnalysisRequest request) {
        return ApiResponse.<BatchAnalysisResponse>builder()
                .status("success")
                .message("AI analysis fallback response")
                .data(BatchAnalysisResponse.builder()
                        .overallHealth("UNAVAILABLE")
                        .aiSummary("AI analysis is temporarily unavailable. Please try again.")
                        .recommendation("")
                        .technologySummaries(new ArrayList<>())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

