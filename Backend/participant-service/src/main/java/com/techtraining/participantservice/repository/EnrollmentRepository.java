package com.techtraining.participantservice.repository;

import com.techtraining.participantservice.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByParticipantId(Long participantId);
    List<Enrollment> findByBatchTechnologyId(Long batchTechnologyId);
    boolean existsByParticipantIdAndBatchTechnologyId(Long participantId, Long batchTechnologyId);
}
