package com.techtraining.batchservice.service;

import com.techtraining.batchservice.dto.request.BatchTechnologyRequest;
import com.techtraining.batchservice.dto.response.BatchTechnologyResponse;

import java.util.List;

public interface BatchTechnologyService {
    BatchTechnologyResponse linkBatchAndTechnology(BatchTechnologyRequest request);
    BatchTechnologyResponse getBatchTechnologyById(Long id);
    List<BatchTechnologyResponse> getAllBatchTechnologies();
    List<BatchTechnologyResponse> getTechnologiesByBatchId(Long batchId);
    List<BatchTechnologyResponse> getBatchesByTechnologyId(Long technologyId);
    BatchTechnologyResponse updateRounds(Long id, Integer totalRounds);
    void deleteBatchTechnology(Long id);
}
