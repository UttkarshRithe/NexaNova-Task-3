package com.techtraining.participantservice.repository;

import com.techtraining.participantservice.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByEmail(String email);
    Optional<Participant> findByEmailAndStatus(String email, com.techtraining.common.enums.EntityStatus status);
    java.util.List<Participant> findByStatus(com.techtraining.common.enums.EntityStatus status);
    org.springframework.data.domain.Page<Participant> findByStatus(com.techtraining.common.enums.EntityStatus status, org.springframework.data.domain.Pageable pageable);
    boolean existsByEmail(String email);
}
