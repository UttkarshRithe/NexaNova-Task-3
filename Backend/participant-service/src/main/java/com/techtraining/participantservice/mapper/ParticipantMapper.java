package com.techtraining.participantservice.mapper;

import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.entity.Participant;

import java.util.List;

public interface ParticipantMapper {
    ParticipantResponse toResponse(Participant participant);
    Participant toEntity(ParticipantRequest request);
    List<ParticipantResponse> toResponseList(List<Participant> participants);
}
