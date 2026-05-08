package com.techtraining.batchservice.dto.response;

import lombok.Data;

@Data
public class BatchTechnologyResponse {
    private Long id;
    private Long batchId;
    private String batchName;
    private Long technologyId;
    private String technologyName;
    private Integer totalRounds;
}
