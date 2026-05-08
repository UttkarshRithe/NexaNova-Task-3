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
    Optional<User> findByEmailAndIsActiveTrue(String email);
    List<User> findByIsActiveTrue();
    Page<User> findByIsActiveTrue(Pageable pageable);
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    boolean existsByEmail(String email);
}
