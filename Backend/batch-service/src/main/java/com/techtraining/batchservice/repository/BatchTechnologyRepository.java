package com.techtraining.batchservice.repository;

import com.techtraining.batchservice.entity.BatchTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchTechnologyRepository extends JpaRepository<BatchTechnology, Long> {
    List<BatchTechnology> findByBatchId(Long batchId);
    List<BatchTechnology> findByTechnologyId(Long technologyId);
    boolean existsByBatchIdAndTechnologyId(Long batchId, Long technologyId);
}
