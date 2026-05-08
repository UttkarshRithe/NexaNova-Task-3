package com.techtraining.participantservice.mapper;

import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.entity.Participant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T23:01:42+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
public class ParticipantMapperImpl implements ParticipantMapper {

    @Override
    public ParticipantResponse toResponse(Participant participant) {
        if ( participant == null ) {
            return null;
        }

        ParticipantResponse participantResponse = new ParticipantResponse();

        participantResponse.setId( participant.getId() );
        participantResponse.setName( participant.getName() );
        participantResponse.setEmail( participant.getEmail() );
        participantResponse.setCreatedAt( participant.getCreatedAt() );

        return participantResponse;
    }

    @Override
    public Participant toEntity(ParticipantRequest request) {
        if ( request == null ) {
            return null;
        }

        Participant.ParticipantBuilder participant = Participant.builder();

        participant.name( request.getName() );
        participant.email( request.getEmail() );

        return participant.build();
    }

    @Override
    public List<ParticipantResponse> toResponseList(List<Participant> participants) {
        if ( participants == null ) {
            return null;
        }

        List<ParticipantResponse> list = new ArrayList<ParticipantResponse>( participants.size() );
        for ( Participant participant : participants ) {
            list.add( toResponse( participant ) );
        }

        return list;
    }
}
