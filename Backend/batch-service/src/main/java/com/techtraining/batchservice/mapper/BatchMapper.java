package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.request.BatchRequest;
import com.techtraining.batchservice.dto.response.BatchResponse;
import com.techtraining.batchservice.entity.Batch;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BatchMapper {
    BatchResponse toResponse(Batch batch);
    Batch toEntity(BatchRequest request);
    List<BatchResponse> toResponseList(List<Batch> batches);
}
