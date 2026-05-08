package com.techtraining.batchservice.mapper;

import com.techtraining.batchservice.dto.request.TechnologyRequest;
import com.techtraining.batchservice.dto.response.TechnologyResponse;
import com.techtraining.batchservice.entity.Technology;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:24+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class TechnologyMapperImpl implements TechnologyMapper {

    @Override
    public TechnologyResponse toResponse(Technology technology) {
        if ( technology == null ) {
            return null;
        }

        TechnologyResponse technologyResponse = new TechnologyResponse();

        technologyResponse.setId( technology.getId() );
        technologyResponse.setName( technology.getName() );
        technologyResponse.setCreatedAt( technology.getCreatedAt() );

        return technologyResponse;
    }

    @Override
    public Technology toEntity(TechnologyRequest request) {
        if ( request == null ) {
            return null;
        }

        Technology.TechnologyBuilder technology = Technology.builder();

        technology.name( request.getName() );

        return technology.build();
    }

    @Override
    public List<TechnologyResponse> toResponseList(List<Technology> technologies) {
        if ( technologies == null ) {
            return null;
        }

        List<TechnologyResponse> list = new ArrayList<TechnologyResponse>( technologies.size() );
        for ( Technology technology : technologies ) {
            list.add( toResponse( technology ) );
        }

        return list;
    }
}
