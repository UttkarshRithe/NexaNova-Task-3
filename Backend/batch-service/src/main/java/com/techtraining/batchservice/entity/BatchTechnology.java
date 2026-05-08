package com.techtraining.batchservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batch_technology", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"batch_id", "technology_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchTechnology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technology_id", nullable = false)
    private Technology technology;

    @Column(name = "total_rounds", nullable = false)
    private Integer totalRounds = 1;
}
