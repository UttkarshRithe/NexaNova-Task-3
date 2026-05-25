package com.techtraining.batchservice.service.impl;

import com.techtraining.batchservice.dto.request.BatchTechnologyRequest;
import com.techtraining.batchservice.dto.response.BatchTechnologyResponse;
import com.techtraining.batchservice.entity.Batch;
import com.techtraining.batchservice.entity.BatchTechnology;
import com.techtraining.batchservice.entity.Technology;
import com.techtraining.batchservice.mapper.BatchTechnologyMapper;
import com.techtraining.batchservice.repository.BatchRepository;
import com.techtraining.batchservice.repository.BatchTechnologyRepository;
import com.techtraining.batchservice.repository.TechnologyRepository;
import com.techtraining.batchservice.service.BatchTechnologyService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchTechnologyServiceImpl implements BatchTechnologyService {

    private final BatchTechnologyRepository batchTechnologyRepository;
    private final BatchRepository batchRepository;
    private final TechnologyRepository technologyRepository;
    private final BatchTechnologyMapper batchTechnologyMapper;
    private final com.techtraining.batchservice.client.EnrollmentClient enrollmentClient;

    @Override
    @Transactional
    public BatchTechnologyResponse linkBatchAndTechnology(BatchTechnologyRequest request) {
        if (batchTechnologyRepository.existsByBatchIdAndTechnologyId(request.getBatchId(), request.getTechnologyId())) {
            throw new DuplicateResourceException("This batch and technology are already linked.");
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + request.getBatchId()));
        
        Technology technology = technologyRepository.findById(request.getTechnologyId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.TECH_NOT_FOUND + request.getTechnologyId()));

        BatchTechnology bt = BatchTechnology.builder()
                .batch(batch)
                .technology(technology)
                .totalRounds(request.getTotalRounds())
                .build();

        BatchTechnology saved = batchTechnologyRepository.save(bt);
        return batchTechnologyMapper.toResponse(saved);
    }

    @Override
    public BatchTechnologyResponse getBatchTechnologyById(Long id) {
        BatchTechnology bt = batchTechnologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch-Technology link not found with id: " + id));
        return batchTechnologyMapper.toResponse(bt);
    }

    @Override
    public List<BatchTechnologyResponse> getAllBatchTechnologies() {
        return batchTechnologyMapper.toResponseList(batchTechnologyRepository.findAll());
    }

    @Override
    public List<BatchTechnologyResponse> getTechnologiesByBatchId(Long batchId) {
        return batchTechnologyMapper.toResponseList(batchTechnologyRepository.findByBatchId(batchId));
    }

    @Override
    public List<BatchTechnologyResponse> getBatchesByTechnologyId(Long technologyId) {
        return batchTechnologyMapper.toResponseList(batchTechnologyRepository.findByTechnologyId(technologyId));
    }

    @Override
    @Transactional
    public BatchTechnologyResponse updateRounds(Long id, Integer totalRounds) {
        BatchTechnology bt = batchTechnologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch-Technology link not found with id: " + id));
        bt.setTotalRounds(totalRounds);
        BatchTechnology updated = batchTechnologyRepository.save(bt);
        return batchTechnologyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBatchTechnology(Long id) {
        BatchTechnology bt = batchTechnologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch-Technology link not found with id: " + id));

        // Safety Validation: Block deletion if enrollments exist
        java.util.List<java.util.Map<String, Object>> enrollments = null;
        try {
            enrollments = enrollmentClient.getEnrollmentsByBatchTechnologyInternal(bt.getId());
        } catch (Exception e) {
            // Ignore or log
        }

        if (enrollments != null && !enrollments.isEmpty()) {
            throw new IllegalStateException("Cannot delete rounds configuration: active enrollments exist referencing it.");
        }

        batchTechnologyRepository.deleteById(id);
    }
}
