package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.request.BatchRequest;
import com.techtraining.batchservice.dto.response.BatchResponse;
import com.techtraining.batchservice.entity.Batch;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:24+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class BatchMapperImpl implements BatchMapper {

    @Override
    public BatchResponse toResponse(Batch batch) {
        if ( batch == null ) {
            return null;
        }

        BatchResponse batchResponse = new BatchResponse();

        batchResponse.setId( batch.getId() );
        batchResponse.setName( batch.getName() );
        batchResponse.setStartDate( batch.getStartDate() );
        batchResponse.setEndDate( batch.getEndDate() );
        batchResponse.setCreatedAt( batch.getCreatedAt() );

        return batchResponse;
    }

    @Override
    public Batch toEntity(BatchRequest request) {
        if ( request == null ) {
            return null;
        }

        Batch.BatchBuilder batch = Batch.builder();

        batch.name( request.getName() );
        batch.startDate( request.getStartDate() );
        batch.endDate( request.getEndDate() );

        return batch.build();
    }

    @Override
    public List<BatchResponse> toResponseList(List<Batch> batches) {
        if ( batches == null ) {
            return null;
        }

        List<BatchResponse> list = new ArrayList<BatchResponse>( batches.size() );
        for ( Batch batch : batches ) {
            list.add( toResponse( batch ) );
        }

        return list;
    }
}
