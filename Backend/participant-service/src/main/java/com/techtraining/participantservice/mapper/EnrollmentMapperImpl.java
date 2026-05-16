package com.techtraining.participantservice.mapper;

import com.techtraining.participantservice.dto.response.EnrollmentResponse;
import com.techtraining.participantservice.entity.Enrollment;
import com.techtraining.participantservice.entity.Participant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnrollmentMapperImpl implements EnrollmentMapper {

    @Override
    public EnrollmentResponse toResponse(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        EnrollmentResponse enrollmentResponse = new EnrollmentResponse();

        enrollmentResponse.setParticipantId(enrollmentParticipantId(enrollment));
        enrollmentResponse.setParticipantName(enrollmentParticipantName(enrollment));
        enrollmentResponse.setId(enrollment.getId());
        enrollmentResponse.setBatchTechnologyId(enrollment.getBatchTechnologyId());
        enrollmentResponse.setEnrolledAt(enrollment.getEnrolledAt());

        return enrollmentResponse;
    }

    @Override
    public List<EnrollmentResponse> toResponseList(List<Enrollment> enrollments) {
        if (enrollments == null) {
            return null;
        }

        List<EnrollmentResponse> list = new ArrayList<EnrollmentResponse>(enrollments.size());
        for (Enrollment enrollment : enrollments) {
            list.add(toResponse(enrollment));
        }

        return list;
    }

    private Long enrollmentParticipantId(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        Participant participant = enrollment.getParticipant();
        if (participant == null) {
            return null;
        }
        Long id = participant.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private String enrollmentParticipantName(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        Participant participant = enrollment.getParticipant();
        if (participant == null) {
            return null;
        }
        String name = participant.getName();
        if (name == null) {
            return null;
        }
        return name;
    }
}
