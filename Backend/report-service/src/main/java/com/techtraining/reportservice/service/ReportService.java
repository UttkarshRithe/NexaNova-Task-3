package com.techtraining.reportservice.service;

import com.techtraining.reportservice.dto.BatchAnalysisResponse;
import com.techtraining.reportservice.dto.ReportResponse;
import java.io.ByteArrayInputStream;

public interface ReportService {
    ReportResponse getBatchReport(Long batchId);
    ReportResponse getParticipantReport(Long participantId);
    ByteArrayInputStream exportBatchReport(Long batchId, String format);
    ByteArrayInputStream exportParticipantReport(Long participantId, String format);
    BatchAnalysisResponse getBatchAiAnalysis(Long batchId);
    ByteArrayInputStream exportBatchAnalysisPdf(Long batchId);
    void emailParticipantReport(Long participantId);
}
