package com.techtraining.participantservice.repository;

import com.techtraining.participantservice.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByParticipantId(Long participantId);
    List<Enrollment> findByParticipantIdAndStatus(Long participantId, com.techtraining.common.enums.EntityStatus status);
    List<Enrollment> findByBatchTechnologyId(Long batchTechnologyId);
    List<Enrollment> findByBatchTechnologyIdAndStatus(Long batchTechnologyId, com.techtraining.common.enums.EntityStatus status);
    List<Enrollment> findByStatus(com.techtraining.common.enums.EntityStatus status);
    org.springframework.data.domain.Page<Enrollment> findByStatus(com.techtraining.common.enums.EntityStatus status, org.springframework.data.domain.Pageable pageable);
    boolean existsByParticipantIdAndBatchTechnologyId(Long participantId, Long batchTechnologyId);
    boolean existsByParticipantIdAndBatchTechnologyIdAndStatus(Long participantId, Long batchTechnologyId, com.techtraining.common.enums.EntityStatus status);
}
