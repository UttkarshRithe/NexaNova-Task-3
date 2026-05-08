package com.techtraining.participantservice.mapper;

import com.techtraining.participantservice.dto.response.EnrollmentResponse;
import com.techtraining.participantservice.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface EnrollmentMapper {
    @Mapping(target = "participantId", source = "participant.id")
    @Mapping(target = "participantName", source = "participant.name")
    EnrollmentResponse toResponse(Enrollment enrollment);
    
    List<EnrollmentResponse> toResponseList(List<Enrollment> enrollments);
}
