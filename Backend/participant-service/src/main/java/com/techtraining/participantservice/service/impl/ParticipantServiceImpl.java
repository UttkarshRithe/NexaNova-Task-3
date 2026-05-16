package com.techtraining.participantservice.service.impl;

import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.exception.DuplicateResourceException;
import com.techtraining.common.exception.ResourceNotFoundException;
import com.techtraining.participantservice.dto.request.ParticipantRequest;
import com.techtraining.participantservice.dto.response.ParticipantResponse;
import com.techtraining.participantservice.entity.Participant;
import com.techtraining.participantservice.mapper.ParticipantMapper;
import com.techtraining.participantservice.repository.ParticipantRepository;
import com.techtraining.participantservice.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    @Override
    @Transactional
    public ParticipantResponse addParticipant(ParticipantRequest request) {
        if (participantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Participant already exists with email: " + request.getEmail());
        }
        Participant participant = participantMapper.toEntity(request);
        Participant saved = participantRepository.save(participant);
        return participantMapper.toResponse(saved);
    }

    @Override
    public ParticipantResponse getParticipantById(Long id) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PARTICIPANT_NOT_FOUND + id));
        return participantMapper.toResponse(participant);
    }

    @Override
    public Page<ParticipantResponse> getAllParticipants(Pageable pageable) {
        return participantRepository.findAll(pageable).map(participantMapper::toResponse);
    }
    
    @Override
    public java.util.List<ParticipantResponse> getAllParticipants() {
        return participantRepository.findAll().stream()
                .map(participantMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }


    @Override
    @Transactional
    public ParticipantResponse updateParticipant(Long id, ParticipantRequest request) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PARTICIPANT_NOT_FOUND + id));
        
        participant.setName(request.getName());
        participant.setEmail(request.getEmail());
        
        Participant updated = participantRepository.save(participant);
        return participantMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteParticipant(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException(AppConstants.PARTICIPANT_NOT_FOUND + id);
        }
        participantRepository.deleteById(id);
    }
}
