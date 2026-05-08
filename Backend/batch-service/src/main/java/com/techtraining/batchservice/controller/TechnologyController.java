package com.techtraining.batchservice.controller;

import com.techtraining.batchservice.dto.request.TechnologyRequest;
import com.techtraining.batchservice.dto.response.TechnologyResponse;
import com.techtraining.batchservice.service.TechnologyService;
import com.techtraining.common.constants.AppConstants;
import com.techtraining.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technologies")
@RequiredArgsConstructor
public class TechnologyController {

    private final TechnologyService technologyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TechnologyResponse>> createTechnology(@Valid @RequestBody TechnologyRequest request) {
        TechnologyResponse response = technologyService.createTechnology(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.TECH_CREATED, response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TechnologyResponse>> getTechnology(@PathVariable Long id) {
        TechnologyResponse response = technologyService.getTechnologyById(id);
        return ResponseEntity.ok(ApiResponse.success("Technology fetched successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TechnologyResponse>>> getAllTechnologies() {
        List<TechnologyResponse> response = technologyService.getAllTechnologies();
        return ResponseEntity.ok(ApiResponse.success("Technologies fetched successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TechnologyResponse>> updateTechnology(@PathVariable Long id, @Valid @RequestBody TechnologyRequest request) {
        TechnologyResponse response = technologyService.updateTechnology(id, request);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.TECH_UPDATED, response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTechnology(@PathVariable Long id) {
        technologyService.deleteTechnology(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.TECH_DELETED, null));
    }
}
