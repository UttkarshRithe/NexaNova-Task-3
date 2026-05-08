package com.techtraining.participantservice.service;

import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipantService {
    ParticipantResponse addParticipant(ParticipantRequest request);
    ParticipantResponse getParticipantById(Long id);
    Page<ParticipantResponse> getAllParticipants(Pageable pageable);
    ParticipantResponse updateParticipant(Long id, ParticipantRequest request);
    void deleteParticipant(Long id);
}
