package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.response.BatchTechnologyResponse;
import com.techtraining.batchservice.entity.Batch;
import com.techtraining.batchservice.entity.BatchTechnology;
import com.techtraining.batchservice.entity.Technology;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:24+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class BatchTechnologyMapperImpl implements BatchTechnologyMapper {

    @Override
    public BatchTechnologyResponse toResponse(BatchTechnology bt) {
        if ( bt == null ) {
            return null;
        }

        BatchTechnologyResponse batchTechnologyResponse = new BatchTechnologyResponse();

        batchTechnologyResponse.setBatchId( btBatchId( bt ) );
        batchTechnologyResponse.setBatchName( btBatchName( bt ) );
        batchTechnologyResponse.setTechnologyId( btTechnologyId( bt ) );
        batchTechnologyResponse.setTechnologyName( btTechnologyName( bt ) );
        batchTechnologyResponse.setId( bt.getId() );
        batchTechnologyResponse.setTotalRounds( bt.getTotalRounds() );

        return batchTechnologyResponse;
    }

    @Override
    public List<BatchTechnologyResponse> toResponseList(List<BatchTechnology> bts) {
        if ( bts == null ) {
            return null;
        }

        List<BatchTechnologyResponse> list = new ArrayList<BatchTechnologyResponse>( bts.size() );
        for ( BatchTechnology batchTechnology : bts ) {
            list.add( toResponse( batchTechnology ) );
        }

        return list;
    }

    private Long btBatchId(BatchTechnology batchTechnology) {
        if ( batchTechnology == null ) {
            return null;
        }
        Batch batch = batchTechnology.getBatch();
        if ( batch == null ) {
            return null;
        }
        Long id = batch.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String btBatchName(BatchTechnology batchTechnology) {
        if ( batchTechnology == null ) {
            return null;
        }
        Batch batch = batchTechnology.getBatch();
        if ( batch == null ) {
            return null;
        }
        String name = batch.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long btTechnologyId(BatchTechnology batchTechnology) {
        if ( batchTechnology == null ) {
            return null;
        }
        Technology technology = batchTechnology.getTechnology();
        if ( technology == null ) {
            return null;
        }
        Long id = technology.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String btTechnologyName(BatchTechnology batchTechnology) {
        if ( batchTechnology == null ) {
            return null;
        }
        Technology technology = batchTechnology.getTechnology();
        if ( technology == null ) {
            return null;
        }
        String name = technology.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
