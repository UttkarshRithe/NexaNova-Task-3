package com.techtraining.batchservice.service.impl;

import com.techtraining.batchservice.dto.request.TechnologyRequest;
import com.techtraining.batchservice.dto.response.TechnologyResponse;
import com.techtraining.batchservice.entity.Technology;
import com.techtraining.batchservice.mapper.TechnologyMapper;
import com.techtraining.batchservice.repository.TechnologyRepository;
import com.techtraining.batchservice.service.TechnologyService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnologyServiceImpl implements TechnologyService {

    private final TechnologyRepository technologyRepository;
    private final TechnologyMapper technologyMapper;
    private final com.techtraining.batchservice.repository.BatchTechnologyRepository batchTechnologyRepository;
    private final com.techtraining.batchservice.client.EnrollmentClient enrollmentClient;

    @Override
    @Transactional
    @CacheEvict(value = "technologies", allEntries = true)
    public TechnologyResponse createTechnology(TechnologyRequest request) {
        if (technologyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Technology already exists with name: " + request.getName());
        }
        Technology technology = technologyMapper.toEntity(request);
        Technology saved = technologyRepository.save(technology);
        return technologyMapper.toResponse(saved);
    }

    @Override
    @Cacheable(value = "technologies", key = "#id")
    public TechnologyResponse getTechnologyById(Long id) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.TECH_NOT_FOUND + id));
        return technologyMapper.toResponse(technology);
    }

    @Override
    @Cacheable(value = "technologies")
    public List<TechnologyResponse> getAllTechnologies() {
        return technologyMapper.toResponseList(technologyRepository.findByStatus(com.techtraining.common.enums.EntityStatus.ACTIVE));
    }

    @Override
    @Transactional
    @CacheEvict(value = "technologies", allEntries = true)
    public TechnologyResponse updateTechnology(Long id, TechnologyRequest request) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.TECH_NOT_FOUND + id));
        technology.setName(request.getName());
        Technology updated = technologyRepository.save(technology);
        return technologyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "technologies", allEntries = true)
    public void deleteTechnology(Long id) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.TECH_NOT_FOUND + id));

        // Safety Validation: Block Technology deletion if any enrollments exist referencing it.
        java.util.List<com.techtraining.batchservice.entity.BatchTechnology> bts = batchTechnologyRepository.findByTechnologyId(id);
        for (com.techtraining.batchservice.entity.BatchTechnology bt : bts) {
            java.util.List<java.util.Map<String, Object>> enrollments = null;
            try {
                enrollments = enrollmentClient.getEnrollmentsByBatchTechnologyInternal(bt.getId());
            } catch (Exception e) {
                // Ignore or log
            }

            if (enrollments != null && !enrollments.isEmpty()) {
                throw new IllegalStateException("Technology deletion blocked: active enrollments exist referencing this technology.");
            }
        }

        technology.setStatus(com.techtraining.common.enums.EntityStatus.ARCHIVED);
        technologyRepository.save(technology);
    }
}
