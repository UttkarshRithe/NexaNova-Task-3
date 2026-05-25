package com.techtraining.userservice.config;

import com.techtraining.userservice.entity.User;
import com.techtraining.userservice.enums.UserRole;
import com.techtraining.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "docker", "local"})
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("User service data already exists — skipping seed");
            return;
        }

        log.info("========== Seeding user-service data ==========");
        seedUsers();
        log.info("========== user-service seeding complete ==========");
    }

    private void seedUsers() {
        String adminPass = passwordEncoder.encode("Admin@123");
        String evalPass  = passwordEncoder.encode("Evaluator@123");

        List<User> users = List.of(
            User.builder()
                .name("Super Admin")
                .email("admin@evaltrack.com")
                .passwordHash(adminPass)
                .role(UserRole.ADMIN)
                .isActive(true)
                .build(),
            User.builder()
                .name("Pranav Sharma")
                .email("pranav@evaltrack.com")
                .passwordHash(evalPass)
                .role(UserRole.EVALUATOR)
                .isActive(true)
                .build(),
            User.builder()
                .name("Sneha Verma")
                .email("sneha@evaltrack.com")
                .passwordHash(evalPass)
                .role(UserRole.EVALUATOR)
                .isActive(true)
                .build(),
            User.builder()
                .name("Rahul Mehta")
                .email("rahul.m@evaltrack.com")
                .passwordHash(evalPass)
                .role(UserRole.EVALUATOR)
                .isActive(true)
                .build(),
            User.builder()
                .name("Priya Joshi")
                .email("priya.j@evaltrack.com")
                .passwordHash(evalPass)
                .role(UserRole.EVALUATOR)
                .isActive(true)
                .build(),
            User.builder()
                .name("Vikram Desai")
                .email("vikram.d@evaltrack.com")
                .passwordHash(evalPass)
                .role(UserRole.EVALUATOR)
                .isActive(true)
                .build()
        );

        userRepository.saveAll(users);
        log.info("Seeded {} users (1 admin + 5 evaluators)", users.size());
        log.info("Admin login     → admin@evaltrack.com  / Admin@123");
        log.info("Evaluator login → pranav@evaltrack.com / Evaluator@123");
    }
}
