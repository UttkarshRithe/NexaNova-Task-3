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
        return batchRepository.findAll(pageable).map(batchMapper::toResponse);
    }

    @Override
    @Transactional
    public BatchResponse updateBatch(Long id, BatchRequest request) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + id));
        
        batch.setName(request.getName());
        batch.setStartDate(request.getStartDate());
        batch.setEndDate(request.getEndDate());
        
        Batch updatedBatch = batchRepository.save(batch);
        return batchMapper.toResponse(updatedBatch);
    }

    @Override
    @Transactional
    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.BATCH_NOT_FOUND + id);
        }
        batchRepository.deleteById(id);
    }
}
