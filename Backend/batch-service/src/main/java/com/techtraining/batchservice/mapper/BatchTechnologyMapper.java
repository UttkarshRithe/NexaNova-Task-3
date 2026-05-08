package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.response.BatchTechnologyResponse;
import com.techtraining.batchservice.entity.BatchTechnology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BatchTechnologyMapper {
    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "batchName", source = "batch.name")
    @Mapping(target = "technologyId", source = "technology.id")
    @Mapping(target = "technologyName", source = "technology.name")
    BatchTechnologyResponse toResponse(BatchTechnology bt);
    
    List<BatchTechnologyResponse> toResponseList(List<BatchTechnology> bts);
}
