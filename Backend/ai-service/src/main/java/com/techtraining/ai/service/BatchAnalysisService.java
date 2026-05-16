package com.techtraining.ai.service;

import com.techtraining.ai.dto.request.BatchAnalysisRequest;
import com.techtraining.ai.dto.response.BatchAnalysisResponse;

public interface BatchAnalysisService {
    BatchAnalysisResponse analyzeBatch(BatchAnalysisRequest request);
}

