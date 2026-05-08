package com.techtraining.reportservice.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.techtraining.reportservice.client.*;
import com.techtraining.reportservice.dto.ReportResponse;
import com.techtraining.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final EvaluationClient evaluationClient;
    private final BatchClient batchClient;
    private final ParticipantClient participantClient;
    private final UserClient userClient;

    @Override
    public ReportResponse getBatchTechnologyReport(Long batchId, Long techId) {
        // Find the specific Batch-Technology link
        List<BatchClient.BatchTechnologyResponse> bts = batchClient.getTechnologiesByBatchId(batchId);
        Long btId = bts.stream()
                .filter(bt -> bt.getTechnologyId().equals(techId))
                .map(BatchClient.BatchTechnologyResponse::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Link not found"));

        BatchClient.BatchResponse batch = batchClient.getBatchById(batchId);
        BatchClient.TechnologyResponse tech = batchClient.getTechnologyById(techId);

        List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByBatchTechnology(btId);
        List<ReportResponse.ParticipantScore> participantScores = new ArrayList<>();

        for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
            ParticipantClient.ParticipantResponse participant = participantClient.getParticipantById(enrollment.getParticipantId());
            List<EvaluationClient.AssignmentResponse> assignments = evaluationClient.getAssignmentsByEnrollment(enrollment.getId());
            
            List<ReportResponse.RoundScore> rounds = new ArrayList<>();
            double totalScore = 0;
            int completedRounds = 0;

            for (EvaluationClient.AssignmentResponse assignment : assignments) {
                if ("COMPLETED".equals(assignment.getStatus())) {
                    EvaluationClient.ResultResponse result = evaluationClient.getResultByAssignment(assignment.getId());
                    UserClient.UserResponse evaluator = userClient.getUserById(assignment.getEvaluatorId());
                    
                    rounds.add(ReportResponse.RoundScore.builder()
                            .roundNumber(assignment.getRoundNumber())
                            .score(result.getScore())
                            .evaluatorName(evaluator.getName())
                            .build());
                    
                    totalScore += result.getScore();
                    completedRounds++;
                }
            }

            participantScores.add(ReportResponse.ParticipantScore.builder()
                    .participantName(participant.getName())
                    .rounds(rounds)
                    .averageScore(completedRounds > 0 ? totalScore / completedRounds : 0.0)
                    .build());
        }

        return ReportResponse.builder()
                .batchName(batch.getName())
                .technologyName(tech.getName())
                .scores(participantScores)
                .build();
    }

    @Override
    public ByteArrayInputStream exportReport(Long batchId, Long techId, String format) {
        ReportResponse data = getBatchTechnologyReport(batchId, techId);
        if ("pdf".equalsIgnoreCase(format)) {
            return generatePdf(data);
        } else {
            return generateCsv(data);
        }
    }

    private ByteArrayInputStream generatePdf(ReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Evaluation Report: " + data.getBatchName() + " - " + data.getTechnologyName()));
            
            Table table = new Table(3);
            table.addHeaderCell("Participant");
            table.addHeaderCell("Average Score");
            table.addHeaderCell("Rounds Completed");

            for (ReportResponse.ParticipantScore ps : data.getScores()) {
                table.addCell(ps.getParticipantName());
                table.addCell(String.format("%.2f", ps.getAverageScore()));
                table.addCell(String.valueOf(ps.getRounds().size()));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateCsv(ReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader("Participant", "Average Score", "Rounds Completed"))) {
            for (ReportResponse.ParticipantScore ps : data.getScores()) {
                csvPrinter.printRecord(ps.getParticipantName(), String.format("%.2f", ps.getAverageScore()), ps.getRounds().size());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
