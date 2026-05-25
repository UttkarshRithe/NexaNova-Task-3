package com.techtraining.participantservice.config;

import com.techtraining.participantservice.entity.Enrollment;
import com.techtraining.participantservice.entity.Participant;
import com.techtraining.participantservice.repository.EnrollmentRepository;
import com.techtraining.participantservice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "docker", "local"})
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ParticipantRepository participantRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void run(String... args) {
        if (participantRepository.count() > 0) {
            log.info("Participant service data already exists — skipping seed");
            return;
        }

        log.info("========== Seeding participant-service data ==========");
        seedParticipants();
        seedEnrollments();
        log.info("========== participant-service seeding complete ==========");
    }

    private void seedParticipants() {
        List<Participant> participants = List.of(
            Participant.builder().name("Yash Rithe").email("yash.rithe@gmail.com").build(),
            Participant.builder().name("Amit Patil").email("amit.patil@gmail.com").build(),
            Participant.builder().name("Neha Singh").email("neha.singh@gmail.com").build(),
            Participant.builder().name("Rohit Kumar").email("rohit.kumar@gmail.com").build(),
            Participant.builder().name("Pooja Sharma").email("pooja.sharma@gmail.com").build(),
            Participant.builder().name("Saurabh Jain").email("saurabh.jain@gmail.com").build(),
            Participant.builder().name("Anjali Gupta").email("anjali.gupta@gmail.com").build(),
            Participant.builder().name("Meera Nair").email("meera.nair@gmail.com").build(),
            Participant.builder().name("Arjun Reddy").email("arjun.reddy@gmail.com").build(),
            Participant.builder().name("Kiran Patil").email("kiran.patil@gmail.com").build(),
            Participant.builder().name("Divya Menon").email("divya.menon@gmail.com").build(),
            Participant.builder().name("Raj Thakur").email("raj.thakur@gmail.com").build()
        );
        participantRepository.saveAll(participants);
        log.info("Seeded {} participants", participants.size());
    }

    private void seedEnrollments() {
        Participant yash    = participantRepository.findByEmail("yash.rithe@gmail.com").orElseThrow();
        Participant amit    = participantRepository.findByEmail("amit.patil@gmail.com").orElseThrow();
        Participant neha    = participantRepository.findByEmail("neha.singh@gmail.com").orElseThrow();
        Participant rohit   = participantRepository.findByEmail("rohit.kumar@gmail.com").orElseThrow();
        Participant pooja   = participantRepository.findByEmail("pooja.sharma@gmail.com").orElseThrow();
        Participant saurabh = participantRepository.findByEmail("saurabh.jain@gmail.com").orElseThrow();
        Participant anjali  = participantRepository.findByEmail("anjali.gupta@gmail.com").orElseThrow();
        Participant meera   = participantRepository.findByEmail("meera.nair@gmail.com").orElseThrow();
        Participant arjun   = participantRepository.findByEmail("arjun.reddy@gmail.com").orElseThrow();
        Participant kiran   = participantRepository.findByEmail("kiran.patil@gmail.com").orElseThrow();
        Participant divya   = participantRepository.findByEmail("divya.menon@gmail.com").orElseThrow();
        Participant raj     = participantRepository.findByEmail("raj.thakur@gmail.com").orElseThrow();

        List<Enrollment> enrollments = List.of(
            // Yash → 3rd Dec Java + Spring Boot
            Enrollment.builder().participant(yash).batchTechnologyId(1L).build(),
            Enrollment.builder().participant(yash).batchTechnologyId(2L).build(),
            // Amit → 3rd Dec Java + Spring Boot
            Enrollment.builder().participant(amit).batchTechnologyId(1L).build(),
            Enrollment.builder().participant(amit).batchTechnologyId(2L).build(),
            // Neha → 6th Jan Java + React
            Enrollment.builder().participant(neha).batchTechnologyId(3L).build(),
            Enrollment.builder().participant(neha).batchTechnologyId(4L).build(),
            // Rohit → 6th Jan Java + Spring Boot
            Enrollment.builder().participant(rohit).batchTechnologyId(3L).build(),
            Enrollment.builder().participant(rohit).batchTechnologyId(5L).build(),
            // Pooja → Feb Python + Data Science
            Enrollment.builder().participant(pooja).batchTechnologyId(6L).build(),
            Enrollment.builder().participant(pooja).batchTechnologyId(7L).build(),
            // Saurabh → Feb Python
            Enrollment.builder().participant(saurabh).batchTechnologyId(6L).build(),
            // Anjali → Mar React
            Enrollment.builder().participant(anjali).batchTechnologyId(8L).build(),
            // Meera → Mar React
            Enrollment.builder().participant(meera).batchTechnologyId(8L).build(),
            // Arjun → 6th Jan React
            Enrollment.builder().participant(arjun).batchTechnologyId(4L).build(),
            // Kiran → 3rd Dec Java
            Enrollment.builder().participant(kiran).batchTechnologyId(1L).build(),
            // Divya → Mar SQL
            Enrollment.builder().participant(divya).batchTechnologyId(9L).build(),
            // Raj → Feb Data Science
            Enrollment.builder().participant(raj).batchTechnologyId(7L).build()
        );

        enrollmentRepository.saveAll(enrollments);
        log.info("Seeded {} enrollments", enrollments.size());
    }
}
