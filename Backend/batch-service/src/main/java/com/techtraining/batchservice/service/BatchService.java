package com.techtraining.batchservice.service;

import com.techtraining.batchservice.dto.request.BatchRequest;
import com.techtraining.batchservice.dto.response.BatchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatchService {
    BatchResponse createBatch(BatchRequest request);
    BatchResponse getBatchById(Long id);
    Page<BatchResponse> getAllBatches(Pageable pageable);
    BatchResponse updateBatch(Long id, BatchRequest request);
    void deleteBatch(Long id);
}
