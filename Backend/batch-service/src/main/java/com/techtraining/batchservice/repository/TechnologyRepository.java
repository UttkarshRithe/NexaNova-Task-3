package com.techtraining.batchservice.repository;

import com.techtraining.batchservice.entity.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    boolean existsByName(String name);
    Optional<Technology> findByName(String name);
}
