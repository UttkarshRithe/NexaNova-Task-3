package com.techtraining.batchservice.controller;

import com.techtraining.batchservice.dto.request.BatchTechnologyRequest;
import com.techtraining.batchservice.dto.response.BatchTechnologyResponse;
import com.techtraining.batchservice.service.BatchTechnologyService;
import com.techtraining.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batch-technology")
@RequiredArgsConstructor
public class BatchTechnologyController {

    private final BatchTechnologyService batchTechnologyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchTechnologyResponse>> linkBatchAndTechnology(@Valid @RequestBody BatchTechnologyRequest request) {
        BatchTechnologyResponse response = batchTechnologyService.linkBatchAndTechnology(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Batch and Technology linked successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchTechnologyResponse>> getBatchTechnology(@PathVariable Long id) {
        BatchTechnologyResponse response = batchTechnologyService.getBatchTechnologyById(id);
        return ResponseEntity.ok(ApiResponse.success("Link fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BatchTechnologyResponse>>> getAllBatchTechnologies() {
        List<BatchTechnologyResponse> response = batchTechnologyService.getAllBatchTechnologies();
        return ResponseEntity.ok(ApiResponse.success("Links fetched successfully", response));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BatchTechnologyResponse>>> getTechnologiesByBatchId(@PathVariable Long batchId) {
        List<BatchTechnologyResponse> response = batchTechnologyService.getTechnologiesByBatchId(batchId);
        return ResponseEntity.ok(ApiResponse.success("Technologies for batch fetched successfully", response));
    }

    @GetMapping("/technology/{techId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BatchTechnologyResponse>>> getBatchesByTechnologyId(@PathVariable Long techId) {
        List<BatchTechnologyResponse> response = batchTechnologyService.getBatchesByTechnologyId(techId);
        return ResponseEntity.ok(ApiResponse.success("Batches for technology fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchTechnologyResponse>> updateRounds(@PathVariable Long id, @RequestParam Integer totalRounds) {
        BatchTechnologyResponse response = batchTechnologyService.updateRounds(id, totalRounds);
        return ResponseEntity.ok(ApiResponse.success("Rounds updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBatchTechnology(@PathVariable Long id) {
        batchTechnologyService.deleteBatchTechnology(id);
        return ResponseEntity.ok(ApiResponse.success("Link removed successfully", null));
    }
}
