package com.techtraining.reportservice.service;

import com.techtraining.reportservice.dto.ReportResponse;
import java.io.ByteArrayInputStream;

public interface ReportService {
    ReportResponse getBatchTechnologyReport(Long batchId, Long techId);
    ByteArrayInputStream exportReport(Long batchId, Long techId, String format);
}
