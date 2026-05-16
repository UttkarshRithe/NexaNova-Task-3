package com.techtraining.ai.controller;

import com.techtraining.ai.dto.request.BatchAnalysisRequest;
import com.techtraining.ai.dto.request.FeedbackRequest;
import com.techtraining.ai.dto.response.BatchAnalysisResponse;
import com.techtraining.ai.dto.response.FeedbackResponse;
import com.techtraining.ai.service.BatchAnalysisService;
import com.techtraining.ai.service.FeedbackGenerationService;
import com.techtraining.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final FeedbackGenerationService feedbackGenerationService;
    private final BatchAnalysisService batchAnalysisService;

    @PostMapping("/generate-feedback")
    public ResponseEntity<ApiResponse<FeedbackResponse>> generateFeedback(@Valid @RequestBody FeedbackRequest request) {
        log.info("Generating AI feedback for technology: {}", request.getTechnology());
        FeedbackResponse response = feedbackGenerationService.generateFeedback(request);
        return ResponseEntity.ok(ApiResponse.success("AI feedback generated successfully", response));
    }

    @PostMapping("/generate-batch-analysis")
    public ResponseEntity<ApiResponse<BatchAnalysisResponse>> generateBatchAnalysis(@Valid @RequestBody BatchAnalysisRequest request) {
        log.info("Generating batch analysis for: {}", request.getBatchName());
        BatchAnalysisResponse response = batchAnalysisService.analyzeBatch(request);
        return ResponseEntity.ok(ApiResponse.success("Batch analysis generated successfully", response));
    }
}

