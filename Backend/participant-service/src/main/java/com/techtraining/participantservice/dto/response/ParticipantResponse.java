package com.techtraining.participantservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipantResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
