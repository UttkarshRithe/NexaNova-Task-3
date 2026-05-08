package com.techtraining.participantservice.config;

import com.techtraining.participantservice.mapper.EnrollmentMapper;
import com.techtraining.participantservice.mapper.ParticipantMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public EnrollmentMapper enrollmentMapper() {
        return Mappers.getMapper(EnrollmentMapper.class);
    }

    @Bean
    public ParticipantMapper participantMapper() {
        return Mappers.getMapper(ParticipantMapper.class);
    }
}