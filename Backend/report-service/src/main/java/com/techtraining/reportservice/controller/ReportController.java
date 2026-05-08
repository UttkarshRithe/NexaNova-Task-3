package com.techtraining.reportservice.controller;

import com.techtraining.common.dto.ApiResponse;
import com.techtraining.reportservice.dto.ReportResponse;
import com.techtraining.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/batch/{bId}/technology/{tId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(@PathVariable Long bId, @PathVariable Long tId) {
        ReportResponse response = reportService.getBatchTechnologyReport(bId, tId);
        return ResponseEntity.ok(ApiResponse.success("Report fetched successfully", response));
    }

    @GetMapping("/batch/{bId}/technology/{tId}/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportReport(@PathVariable Long bId, @PathVariable Long tId, @RequestParam String format) {
        ByteArrayInputStream bis = reportService.exportReport(bId, tId, format);
        
        HttpHeaders headers = new HttpHeaders();
        String fileName = "report_" + bId + "_" + tId + "." + format;
        headers.add("Content-Disposition", "attachment; filename=" + fileName);

        MediaType mediaType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(bis));
    }
}
