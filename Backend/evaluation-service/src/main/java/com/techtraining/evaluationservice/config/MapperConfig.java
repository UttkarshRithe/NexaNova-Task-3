package com.techtraining.evaluationservice.config;

import com.techtraining.evaluationservice.mapper.AssignmentMapper;
import com.techtraining.evaluationservice.mapper.ResultMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public AssignmentMapper assignmentMapper() {
        return Mappers.getMapper(AssignmentMapper.class);
    }

    @Bean
    public ResultMapper resultMapper() {
        return Mappers.getMapper(ResultMapper.class);
    }
}