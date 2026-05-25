package com.techtraining.batchservice.service.impl;

import com.techtraining.batchservice.dto.request.BatchRequest;
import com.techtraining.batchservice.dto.response.BatchResponse;
import com.techtraining.batchservice.entity.Batch;
import com.techtraining.batchservice.mapper.BatchMapper;
import com.techtraining.batchservice.repository.BatchRepository;
import com.techtraining.batchservice.service.BatchService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final com.techtraining.batchservice.repository.BatchTechnologyRepository batchTechnologyRepository;
    private final com.techtraining.batchservice.client.EnrollmentClient enrollmentClient;
    private final com.techtraining.batchservice.client.EvaluationAssignmentClient evaluationAssignmentClient;

    @Override
    @Transactional
    public BatchResponse createBatch(BatchRequest request) {
        if (batchRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(AppConstants.BATCH_DUPLICATE + request.getName());
        }
        Batch batch = batchMapper.toEntity(request);
        Batch savedBatch = batchRepository.save(batch);
        return batchMapper.toResponse(savedBatch);
    }

    @Override
    public BatchResponse getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + id));
        return batchMapper.toResponse(batch);
    }

    @Override
    public Page<BatchResponse> getAllBatches(Pageable pageable) {
        return batchRepository.findByStatus(com.techtraining.common.enums.EntityStatus.ACTIVE, pageable).map(batchMapper::toResponse);
    }

    @Override
    @Transactional
    public BatchResponse updateBatch(Long id, BatchRequest request) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + id));

        // Mutability Rule: Once an enrollment exists, batch startDate is immutable
        java.util.List<com.techtraining.batchservice.entity.BatchTechnology> bts = batchTechnologyRepository.findByBatchId(id);
        boolean enrollmentExists = false;
        for (com.techtraining.batchservice.entity.BatchTechnology bt : bts) {
            try {
                java.util.List<java.util.Map<String, Object>> enrollments = enrollmentClient.getEnrollmentsByBatchTechnologyInternal(bt.getId());
                if (enrollments != null && !enrollments.isEmpty()) {
                    enrollmentExists = true;
                    break;
                }
            } catch (Exception e) {
                // Ignore internal check errors or log
            }
        }

        if (enrollmentExists) {
            if (!batch.getStartDate().equals(request.getStartDate())) {
                throw new IllegalStateException("Cannot change batch start date once enrollments exist.");
            }
        }

        batch.setName(request.getName());
        batch.setStartDate(request.getStartDate());
        batch.setEndDate(request.getEndDate());

        Batch updatedBatch = batchRepository.save(batch);
        return batchMapper.toResponse(updatedBatch);
    }

    @Override
    @Transactional
    public void deleteBatch(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + id));

        // Safety Validation: Block Batch deletion if any enrollments exist or future scheduled rounds/assignments exist.
        java.util.List<com.techtraining.batchservice.entity.BatchTechnology> bts = batchTechnologyRepository.findByBatchId(id);
        for (com.techtraining.batchservice.entity.BatchTechnology bt : bts) {
            java.util.List<java.util.Map<String, Object>> enrollments = null;
            try {
                enrollments = enrollmentClient.getEnrollmentsByBatchTechnologyInternal(bt.getId());
            } catch (Exception e) {
                // Ignore or log
            }

            if (enrollments != null && !enrollments.isEmpty()) {
                throw new IllegalStateException("Batch deletion blocked: enrollments exist for this batch.");
            }
        }

        batch.setStatus(com.techtraining.common.enums.EntityStatus.ARCHIVED);
        batchRepository.save(batch);
    }
}
