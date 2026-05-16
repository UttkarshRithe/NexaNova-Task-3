package com.techtraining.ai.service;

import com.techtraining.ai.dto.request.FeedbackRequest;
import com.techtraining.ai.dto.response.FeedbackResponse;

public interface FeedbackGenerationService {
    FeedbackResponse generateFeedback(FeedbackRequest request);
}

