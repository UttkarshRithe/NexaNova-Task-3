package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.request.TechnologyRequest;
import com.techtraining.batchservice.dto.response.TechnologyResponse;
import com.techtraining.batchservice.entity.Technology;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TechnologyMapper {
    TechnologyResponse toResponse(Technology technology);
    Technology toEntity(TechnologyRequest request);
    List<TechnologyResponse> toResponseList(List<Technology> technologies);
}
