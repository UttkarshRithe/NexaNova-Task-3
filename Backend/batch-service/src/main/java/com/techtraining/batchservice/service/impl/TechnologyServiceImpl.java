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
        return technologyMapper.toResponseList(technologyRepository.findAll());
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
        if (!technologyRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.TECH_NOT_FOUND + id);
        }
        technologyRepository.deleteById(id);
    }
}
