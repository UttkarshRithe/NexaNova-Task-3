package com.techtraining.evaluationservice.config;

import com.techtraining.evaluationservice.entity.EvaluationAssignment;
import com.techtraining.evaluationservice.entity.EvaluationResult;
import com.techtraining.evaluationservice.enums.AssignmentStatus;
import com.techtraining.evaluationservice.repository.AssignmentRepository;
import com.techtraining.evaluationservice.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "docker", "local"})
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AssignmentRepository assignmentRepository;
    private final ResultRepository resultRepository;

    @Override
    public void run(String... args) {
        if (assignmentRepository.count() > 0) {
            log.info("Evaluation service data already exists — skipping seed");
            return;
        }

        log.info("========== Seeding evaluation-service data ==========");
        List<EvaluationAssignment> assignments = seedAssignments();
        seedResults(assignments);
        log.info("========== evaluation-service seeding complete ==========");
    }

    private List<EvaluationAssignment> seedAssignments() {
        List<EvaluationAssignment> assignments = List.of(
            // Yash → Java Round 1 & 2 (Pranav)
            makeAssignment(1L, 2L, 1, AssignmentStatus.COMPLETED),
            makeAssignment(1L, 2L, 2, AssignmentStatus.COMPLETED),
            // Yash → Spring Boot Round 1 (Sneha)
            makeAssignment(2L, 3L, 1, AssignmentStatus.COMPLETED),
            // Amit → Java Round 1 (Pranav) Round 2 (Rahul)
            makeAssignment(3L, 2L, 1, AssignmentStatus.COMPLETED),
            makeAssignment(3L, 4L, 2, AssignmentStatus.PENDING),
            // Amit → Spring Boot Round 1 (Sneha)
            makeAssignment(4L, 3L, 1, AssignmentStatus.PENDING),
            // Neha → Java Round 1 (Rahul)
            makeAssignment(5L, 4L, 1, AssignmentStatus.COMPLETED),
            // Neha → React Round 1 (Priya)
            makeAssignment(6L, 5L, 1, AssignmentStatus.COMPLETED),
            // Rohit → Java Round 1 (Rahul)
            makeAssignment(7L, 4L, 1, AssignmentStatus.COMPLETED),
            // Rohit → Spring Boot Round 1 (Sneha)
            makeAssignment(8L, 3L, 1, AssignmentStatus.PENDING),
            // Pooja → Python Round 1 (Vikram)
            makeAssignment(9L, 6L, 1, AssignmentStatus.COMPLETED),
            // Pooja → Data Science Round 1 (Priya)
            makeAssignment(10L, 5L, 1, AssignmentStatus.COMPLETED),
            // Saurabh → Python Round 1 (Vikram)
            makeAssignment(11L, 6L, 1, AssignmentStatus.PENDING),
            // Anjali → React Round 1 (Priya)
            makeAssignment(12L, 5L, 1, AssignmentStatus.COMPLETED),
            // Meera → React Round 1 (Priya)
            makeAssignment(13L, 5L, 1, AssignmentStatus.PENDING),
            // Kiran → Java Round 1 (Pranav)
            makeAssignment(15L, 2L, 1, AssignmentStatus.COMPLETED)
        );

        List<EvaluationAssignment> saved = assignmentRepository.saveAll(assignments);
        log.info("Seeded {} evaluation assignments", saved.size());
        return saved;
    }

    private void seedResults(List<EvaluationAssignment> assignments) {
        List<EvaluationResult> results = List.of(
            // Yash → Java Round 1 (score 78)
            makeResult(findAssignment(assignments, 1L, 1), 78,
                80, 65, 75,
                "Good understanding of OOP concepts. Solved problems efficiently.",
                "Exception handling needs improvement. Streams not covered well.",
                null),
            // Yash → Java Round 2 (score 85)
            makeResult(findAssignment(assignments, 1L, 2), 85,
                88, 70, 82,
                "Significant improvement from Round 1. Strong in collections.",
                "Minor issues with lambda expressions.",
                null),
            // Yash → Spring Boot Round 1 (score 72)
            makeResult(findAssignment(assignments, 2L, 1), 72,
                75, 68, 70,
                "Good REST API design. Understands annotations well.",
                "Security configuration needs more practice.",
                null),
            // Amit → Java Round 1 (score 55)
            makeResult(findAssignment(assignments, 3L, 1), 55,
                58, 50, 60,
                "Basic Java syntax and concepts are clear.",
                "Collections and Generics very weak. Needs focused practice.",
                null),
            // Neha → Java Round 1 (score 91)
            makeResult(findAssignment(assignments, 5L, 1), 91,
                95, 88, 90,
                "Excellent understanding across all Java concepts. Outstanding performance.",
                "Minor improvements needed in advanced lambda expressions.",
                null),
            // Neha → React Round 1 (score 88)
            makeResult(findAssignment(assignments, 6L, 1), 88,
                90, 85, 87,
                "Strong component design. Good understanding of hooks.",
                "State management with Redux can be improved.",
                null),
            // Rohit → Java Round 1 (score 63)
            makeResult(findAssignment(assignments, 7L, 1), 63,
                65, 58, 68,
                "Good problem-solving approach. Logical thinking is strong.",
                "Communication skills need improvement. Needs to explain thought process better.",
                null),
            // Pooja → Python Round 1 (score 82)
            makeResult(findAssignment(assignments, 9L, 1), 82,
                85, 80, 78,
                "Strong Python fundamentals. Good coding standards.",
                "Pandas optimization techniques need more practice.",
                null),
            // Pooja → Data Science Round 1 (score 76)
            makeResult(findAssignment(assignments, 10L, 1), 76,
                78, 72, 75,
                "Good understanding of ML concepts and algorithms.",
                "Model tuning and hyperparameter optimization needs practice.",
                null),
            // Anjali → React Round 1 (score 95)
            makeResult(findAssignment(assignments, 12L, 1), 95,
                98, 92, 94,
                "Outstanding performance. Exceptional component architecture and code quality.",
                "Minor CSS styling improvements possible.",
                null),
            // Kiran → Java Round 1 (score 48)
            makeResult(findAssignment(assignments, 15L, 1), 48,
                50, 45, 52,
                "Basic Java syntax is clear. Shows willingness to learn.",
                "OOP concepts very weak. Inheritance and polymorphism not understood. Needs remedial support.",
                null)
        );

        resultRepository.saveAll(results);
        log.info("Seeded {} evaluation results", results.size());
    }

    private EvaluationAssignment makeAssignment(
            Long enrollmentId, Long evaluatorId,
            int roundNumber, AssignmentStatus status) {
        return EvaluationAssignment.builder()
            .enrollmentId(enrollmentId)
            .evaluatorId(evaluatorId)
            .roundNumber(roundNumber)
            .status(status)
            .build();
    }

    private EvaluationAssignment findAssignment(
            List<EvaluationAssignment> list,
            Long enrollmentId, int roundNumber) {
        return list.stream()
            .filter(a -> a.getEnrollmentId().equals(enrollmentId)
                      && a.getRoundNumber() == roundNumber)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                "Assignment not found for enrollmentId: " + enrollmentId
                + " round: " + roundNumber));
    }

    private EvaluationResult makeResult(
            EvaluationAssignment assignment,
            int score,
            Integer technicalScore,
            Integer communicationScore,
            Integer problemSolvingScore,
            String strengths,
            String weaknesses,
            String aiFeedback) {
        return EvaluationResult.builder()
            .assignmentId(assignment.getId())
            .score(score)
            .technicalScore(technicalScore)
            .communicationScore(communicationScore)
            .problemSolvingScore(problemSolvingScore)
            .strengths(strengths)
            .weaknesses(weaknesses)
            .aiFeedback(aiFeedback)
            .build();
    }
}
