package com.techtraining.reportservice.controller;

import com.techtraining.common.dto.ApiResponse;
import com.techtraining.reportservice.dto.BatchAnalysisResponse;
import com.techtraining.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

    @GetMapping("/batch/{batchId}/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportBatchReport(@PathVariable Long batchId, @RequestParam(defaultValue = "pdf") String format) {
        ByteArrayInputStream bis = reportService.exportBatchReport(batchId, format);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=batch_report_" + batchId + "." + format);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType("pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/participant/{participantId}/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportParticipantReport(@PathVariable Long participantId, @RequestParam(defaultValue = "pdf") String format) {
        ByteArrayInputStream bis = reportService.exportParticipantReport(participantId, format);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=participant_report_" + participantId + "." + format);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType("pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/batch/{batchId}/ai-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchAnalysisResponse>> getBatchAiAnalysis(@PathVariable Long batchId) {
        return ResponseEntity.ok(ApiResponse.success("AI analysis generated successfully", reportService.getBatchAiAnalysis(batchId)));
    }

    @GetMapping("/batch/{batchId}/ai-analysis/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportBatchAiAnalysis(@PathVariable Long batchId) {
        ByteArrayInputStream bis = reportService.exportBatchAnalysisPdf(batchId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=batch_ai_analysis_" + batchId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @PostMapping("/participant/{participantId}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> emailParticipantReport(@PathVariable Long participantId) {
        reportService.emailParticipantReport(participantId);
        return ResponseEntity.ok(ApiResponse.success("Participant report email queued successfully", null));
    }
}
