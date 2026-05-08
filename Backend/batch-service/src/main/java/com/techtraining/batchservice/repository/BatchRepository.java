package com.techtraining.batchservice.repository;

import com.techtraining.batchservice.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByName(String name);
}
