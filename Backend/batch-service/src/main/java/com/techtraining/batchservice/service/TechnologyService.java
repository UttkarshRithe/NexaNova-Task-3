package com.techtraining.batchservice.service;

import com.techtraining.batchservice.dto.request.TechnologyRequest;
import com.techtraining.batchservice.dto.response.TechnologyResponse;

import java.util.List;

public interface TechnologyService {
    TechnologyResponse createTechnology(TechnologyRequest request);
    TechnologyResponse getTechnologyById(Long id);
    List<TechnologyResponse> getAllTechnologies();
    TechnologyResponse updateTechnology(Long id, TechnologyRequest request);
    void deleteTechnology(Long id);
}
