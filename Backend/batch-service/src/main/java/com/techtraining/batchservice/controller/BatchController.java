package com.techtraining.batchservice.controller;

import com.techtraining.batchservice.dto.request.BatchRequest;
import com.techtraining.batchservice.dto.response.BatchResponse;
import com.techtraining.batchservice.service.BatchService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(@Valid @RequestBody BatchRequest request) {
        BatchResponse response = batchService.createBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.BATCH_CREATED, response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> getBatch(@PathVariable Long id) {
        BatchResponse response = batchService.getBatchById(id);
        return ResponseEntity.ok(ApiResponse.success("Batch fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BatchResponse>>> getAllBatches(Pageable pageable) {
        Page<BatchResponse> response = batchService.getAllBatches(pageable);
        return ResponseEntity.ok(ApiResponse.success("Batches fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> updateBatch(@PathVariable Long id, @Valid @RequestBody BatchRequest request) {
        BatchResponse response = batchService.updateBatch(id, request);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.BATCH_UPDATED, response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.BATCH_DELETED, null));
    }
}
