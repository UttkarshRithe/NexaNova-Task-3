package com.techtraining.participantservice.mapper;

import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.entity.Participant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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
