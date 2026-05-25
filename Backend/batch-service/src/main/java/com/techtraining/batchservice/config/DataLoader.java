package com.techtraining.batchservice.config;

import com.techtraining.batchservice.entity.Batch;
import com.techtraining.batchservice.entity.BatchTechnology;
import com.techtraining.batchservice.entity.Technology;
import com.techtraining.batchservice.repository.BatchRepository;
import com.techtraining.batchservice.repository.BatchTechnologyRepository;
import com.techtraining.batchservice.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile({"dev", "docker", "local"})
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final TechnologyRepository technologyRepository;
    private final BatchRepository batchRepository;
    private final BatchTechnologyRepository batchTechnologyRepository;

    @Override
    public void run(String... args) {
        if (technologyRepository.count() > 0) {
            log.info("Batch service data already exists — skipping seed");
            return;
        }

        log.info("========== Seeding batch-service data ==========");
        seedTechnologies();
        seedBatches();
        seedBatchTechnologies();
        log.info("========== batch-service seeding complete ==========");
    }

    private void seedTechnologies() {
        List<String> names = List.of(
            "Java", "Python", "React",
            "Spring Boot", "Data Science", "SQL", "DevOps"
        );
        names.forEach(name ->
            technologyRepository.save(
                Technology.builder().name(name).build()
            )
        );
        log.info("Seeded {} technologies", names.size());
    }

    private void seedBatches() {
        List<Batch> batches = List.of(
            Batch.builder()
                .name("3rd Dec Java Batch")
                .startDate(LocalDate.of(2025, 12, 3))
                .endDate(LocalDate.of(2026, 2, 28))
                .build(),
            Batch.builder()
                .name("6th Jan Java Full Stack Batch")
                .startDate(LocalDate.of(2026, 1, 6))
                .endDate(LocalDate.of(2026, 6, 1))
                .build(),
            Batch.builder()
                .name("Feb Python Batch")
                .startDate(LocalDate.of(2026, 2, 1))
                .endDate(LocalDate.of(2026, 4, 30))
                .build(),
            Batch.builder()
                .name("Mar React Batch")
                .startDate(LocalDate.of(2026, 3, 1))
                .endDate(LocalDate.of(2026, 5, 31))
                .build()
        );
        batchRepository.saveAll(batches);
        log.info("Seeded {} batches", batches.size());
    }

    private void seedBatchTechnologies() {
        // Fetch by name to get actual IDs
        Batch b1 = batchRepository.findByName("3rd Dec Java Batch").orElseThrow();
        Batch b2 = batchRepository.findByName("6th Jan Java Full Stack Batch").orElseThrow();
        Batch b3 = batchRepository.findByName("Feb Python Batch").orElseThrow();
        Batch b4 = batchRepository.findByName("Mar React Batch").orElseThrow();

        Technology java        = technologyRepository.findByName("Java").orElseThrow();
        Technology python      = technologyRepository.findByName("Python").orElseThrow();
        Technology react       = technologyRepository.findByName("React").orElseThrow();
        Technology springBoot  = technologyRepository.findByName("Spring Boot").orElseThrow();
        Technology dataScience = technologyRepository.findByName("Data Science").orElseThrow();
        Technology sql         = technologyRepository.findByName("SQL").orElseThrow();
        Technology devops      = technologyRepository.findByName("DevOps").orElseThrow();

        List<BatchTechnology> configs = List.of(
            BatchTechnology.builder().batch(b1).technology(java).totalRounds(2).build(),
            BatchTechnology.builder().batch(b1).technology(springBoot).totalRounds(2).build(),
            BatchTechnology.builder().batch(b2).technology(java).totalRounds(3).build(),
            BatchTechnology.builder().batch(b2).technology(react).totalRounds(2).build(),
            BatchTechnology.builder().batch(b2).technology(springBoot).totalRounds(2).build(),
            BatchTechnology.builder().batch(b3).technology(python).totalRounds(2).build(),
            BatchTechnology.builder().batch(b3).technology(dataScience).totalRounds(1).build(),
            BatchTechnology.builder().batch(b4).technology(react).totalRounds(3).build(),
            BatchTechnology.builder().batch(b4).technology(sql).totalRounds(2).build(),
            BatchTechnology.builder().batch(b4).technology(devops).totalRounds(1).build()
        );

        batchTechnologyRepository.saveAll(configs);
        log.info("Seeded {} batch-technology configurations", configs.size());
    }
}
