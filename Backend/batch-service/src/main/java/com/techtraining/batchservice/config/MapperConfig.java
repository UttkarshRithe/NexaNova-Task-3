package com.techtraining.batchservice.config;

import com.techtraining.batchservice.mapper.BatchMapper;
import com.techtraining.batchservice.mapper.BatchTechnologyMapper;
import com.techtraining.batchservice.mapper.TechnologyMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public BatchMapper batchMapper() {
        return Mappers.getMapper(BatchMapper.class);
    }

    @Bean
    public BatchTechnologyMapper batchTechnologyMapper() {
        return Mappers.getMapper(BatchTechnologyMapper.class);
    }

    @Bean
    public TechnologyMapper technologyMapper() {
        return Mappers.getMapper(TechnologyMapper.class);
    }
}