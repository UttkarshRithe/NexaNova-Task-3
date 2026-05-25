package com.techtraining.batchservice.repository;

import com.techtraining.batchservice.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByName(String name);
    Optional<Batch> findByName(String name);
    java.util.List<Batch> findByStatus(com.techtraining.common.enums.EntityStatus status);
    org.springframework.data.domain.Page<Batch> findByStatus(com.techtraining.common.enums.EntityStatus status, org.springframework.data.domain.Pageable pageable);
}
