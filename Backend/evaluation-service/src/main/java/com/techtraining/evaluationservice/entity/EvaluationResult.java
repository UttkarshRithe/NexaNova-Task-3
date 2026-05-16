package com.techtraining.evaluationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private Long assignmentId;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "technical_score")
    private Integer technicalScore;

    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "problem_solving_score")
    private Integer problemSolvingScore;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}
