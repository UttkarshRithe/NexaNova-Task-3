package com.techtraining.userservice.repository;

import com.techtraining.userservice.entity.User;
import com.techtraining.userservice.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndStatus(String email, com.techtraining.common.enums.EntityStatus status);
    List<User> findByStatus(com.techtraining.common.enums.EntityStatus status);
    Page<User> findByStatus(com.techtraining.common.enums.EntityStatus status, Pageable pageable);
    Page<User> findByRoleAndStatus(UserRole role, com.techtraining.common.enums.EntityStatus status, Pageable pageable);
    List<User> findByRoleAndStatus(UserRole role, com.techtraining.common.enums.EntityStatus status);
    boolean existsByEmail(String email);
}
