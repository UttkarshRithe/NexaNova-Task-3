package com.techtraining.reportservice.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.UnitValue;
import com.techtraining.reportservice.client.*;
import com.techtraining.reportservice.dto.BatchAnalysisRequest;
import com.techtraining.reportservice.dto.BatchAnalysisResponse;
import com.techtraining.reportservice.dto.ReportResponse;
import com.techtraining.reportservice.dto.response.BatchReportResponse;
import com.techtraining.reportservice.dto.response.IndividualReportResponse;
import com.techtraining.reportservice.service.ReportService;
import com.techtraining.common.event.ParticipantReportEmailEvent;
import com.techtraining.reportservice.producer.ParticipantReportEmailProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final EvaluationClient evaluationClient;
    private final BatchClient batchClient;
    private final ParticipantClient participantClient;
    private final UserClient userClient;
    private final AiServiceClient aiServiceClient;
    private final PdfExportService pdfExportService;
    private final ParticipantReportEmailProducer participantReportEmailProducer;

    @Override
    public ReportResponse getBatchReport(Long batchId) {
        BatchClient.BatchResponse batch = batchClient.getBatchById(batchId).getData();
        if (batch == null) throw new RuntimeException("Batch not found");

        List<BatchClient.BatchTechnologyResponse> btsList = batchClient.getTechnologiesByBatchId(batchId).getData();
        List<ReportResponse.TechnologyReport> techReports = new ArrayList<>();

        if (btsList != null) {
            for (BatchClient.BatchTechnologyResponse bts : btsList) {
                techReports.add(fetchTechnologyReport(bts, null));
            }
        }

        return ReportResponse.builder()
                .batchName(batch.getName())
                .technologies(techReports)
                .build();
    }

    @Override
    public ReportResponse getParticipantReport(Long participantId) {
        ParticipantClient.ParticipantResponse participant = participantClient.getParticipantById(participantId).getData();
        if (participant == null) throw new RuntimeException("Participant not found");

        List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByParticipant(participantId).getData();
        List<ReportResponse.TechnologyReport> techReports = new ArrayList<>();

        if (enrollments != null) {
            for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
                BatchClient.BatchTechnologyResponse bts = batchClient.getBatchTechnologyById(enrollment.getBatchTechnologyId()).getData();
                if (bts != null) {
                    techReports.add(fetchTechnologyReport(bts, participantId));
                }
            }
        }

        return ReportResponse.builder()
                .participantName(participant.getName())
                .technologies(techReports)
                .build();
    }

    private ReportResponse.TechnologyReport fetchTechnologyReport(BatchClient.BatchTechnologyResponse bts, Long filterParticipantId) {
        BatchClient.TechnologyResponse tech = batchClient.getTechnologyById(bts.getTechnologyId()).getData();
        List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByBatchTechnology(bts.getId()).getData();
        List<ReportResponse.ParticipantScore> participantScores = new ArrayList<>();

        if (enrollments != null) {
            for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
                if (filterParticipantId != null && !enrollment.getParticipantId().equals(filterParticipantId)) continue;

                ParticipantClient.ParticipantResponse p = participantClient.getParticipantById(enrollment.getParticipantId()).getData();
                List<EvaluationClient.AssignmentResponse> assignments = evaluationClient.getAssignmentsByEnrollment(enrollment.getId()).getData();
                
                List<ReportResponse.RoundScore> rounds = new ArrayList<>();
                double totalScore = 0;
                int completedRounds = 0;

                if (assignments != null) {
                    for (EvaluationClient.AssignmentResponse assignment : assignments) {
                        if ("COMPLETED".equalsIgnoreCase(assignment.getStatus())) {
                            EvaluationClient.ResultResponse result = evaluationClient.getResultByAssignment(assignment.getId()).getData();
                            UserClient.UserResponse evaluator = userClient.getUserById(assignment.getEvaluatorId()).getData();
                            
                            if (result != null) {
                                String feedbackText = result.getAiFeedback() != null ? result.getAiFeedback() : result.getStrengths() != null ? result.getStrengths() : result.getComments();
                                rounds.add(ReportResponse.RoundScore.builder()
                                        .roundNumber(assignment.getRoundNumber())
                                        .score(result.getScore())
                                        .evaluatorName(evaluator != null ? evaluator.getName() : "Unknown")
                                        .feedback(feedbackText)
                                        .build());
                                totalScore += result.getScore();
                                completedRounds++;
                            }
                        }
                    }
                }

                participantScores.add(ReportResponse.ParticipantScore.builder()
                        .participantName(p != null ? p.getName() : "Unknown")
                        .rounds(rounds)
                        .averageScore(completedRounds > 0 ? totalScore / completedRounds : 0.0)
                        .build());
            }
        }

        return ReportResponse.TechnologyReport.builder()
                .technologyName(tech != null ? tech.getName() : "Unknown")
                .participantScores(participantScores)
                .build();
    }

    @Override
    public ByteArrayInputStream exportBatchReport(Long batchId, String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            BatchReportResponse data = fetchBatchReportData(batchId);
            return generateBatchPdf(data);
        }
        ReportResponse data = getBatchReport(batchId);
        return generateCsv(data);
    }

    @Override
    public ByteArrayInputStream exportParticipantReport(Long participantId, String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            IndividualReportResponse data = fetchIndividualReportData(participantId);
            return generateIndividualPdf(data);
        }
        ReportResponse data = getParticipantReport(participantId);
        return generateCsv(data);
    }

    @Override
    public BatchAnalysisResponse getBatchAiAnalysis(Long batchId) {
        BatchClient.BatchResponse batch = batchClient.getBatchById(batchId).getData();
        if (batch == null) throw new RuntimeException("Batch not found");

        List<BatchClient.BatchTechnologyResponse> technologies = batchClient.getTechnologiesByBatchId(batchId).getData();
        if (technologies == null || technologies.isEmpty()) {
            return BatchAnalysisResponse.builder()
                    .overallHealth("UNAVAILABLE")
                    .aiSummary("No technologies found for the selected batch.")
                    .recommendation("")
                    .technologySummaries(new ArrayList<>())
                    .build();
        }

        BatchAnalysisRequest request = buildAnalysisRequest(batch.getName(), technologies);
        return aiServiceClient.generateBatchAnalysis(request).getData();
    }

    @Override
    public ByteArrayInputStream exportBatchAnalysisPdf(Long batchId) {
        BatchAnalysisResponse analysis = getBatchAiAnalysis(batchId);
        BatchClient.BatchResponse batch = batchClient.getBatchById(batchId).getData();
        return generateAnalysisPdf(batch != null ? batch.getName() : "Batch", analysis);
    }

    private ByteArrayInputStream generateAnalysisPdf(String batchName, BatchAnalysisResponse analysis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("🤖 Holistic AI Analysis Report").setFontSize(20).setBold());
            document.add(new Paragraph("Batch: " + batchName).setFontSize(14));
            document.add(new Paragraph("Generated on: " + new Date()).setFontSize(10).setItalic());

            document.add(new Paragraph("\nBatch Health: " + analysis.getOverallHealth()).setBold().setFontSize(14));

            document.add(new Paragraph("\nTechnology Summaries:").setBold());
            Table table = new Table(5);
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell("Technology");
            table.addHeaderCell("Avg Score");
            table.addHeaderCell("Status");
            table.addHeaderCell("At-Risk");
            table.addHeaderCell("Trend");

            for (BatchAnalysisResponse.TechnologySummary tech : analysis.getTechnologySummaries()) {
                table.addCell(tech.getName());
                table.addCell(String.format("%.2f", tech.getAvgScore()));
                table.addCell(tech.getStatus());
                table.addCell(String.valueOf(tech.getAtRiskCount()));
                table.addCell(tech.getTrend());
            }
            document.add(table);

            document.add(new Paragraph("\n📝 Holistic Analysis:").setBold());
            document.add(new Paragraph(analysis.getAiSummary()));

            document.add(new Paragraph("\n💡 Strategic Recommendation:").setBold());
            document.add(new Paragraph(analysis.getRecommendation()));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private BatchAnalysisRequest buildAnalysisRequest(String batchName, List<BatchClient.BatchTechnologyResponse> technologies) {
        List<BatchAnalysisRequest.TechnologyData> techData = new ArrayList<>();
        final int passingScore = 60;

        for (BatchClient.BatchTechnologyResponse bt : technologies) {
            BatchClient.TechnologyResponse technology = batchClient.getTechnologyById(bt.getTechnologyId()).getData();
            if (technology == null) continue;

            List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByBatchTechnology(bt.getId()).getData();
            if (enrollments == null) enrollments = new ArrayList<>();

            List<Double> participantAverages = new ArrayList<>();
            Map<Integer, List<Integer>> roundScores = new HashMap<>();

            for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
                List<EvaluationClient.AssignmentResponse> assignments = evaluationClient.getAssignmentsByEnrollment(enrollment.getId()).getData();
                if (assignments == null) continue;

                int completed = 0;
                double total = 0;

                for (EvaluationClient.AssignmentResponse assignment : assignments) {
                    if (!"COMPLETED".equalsIgnoreCase(assignment.getStatus())) continue;
                    EvaluationClient.ResultResponse result = evaluationClient.getResultByAssignment(assignment.getId()).getData();
                    if (result == null || result.getScore() == null) continue;

                    total += result.getScore();
                    completed++;
                    roundScores.computeIfAbsent(assignment.getRoundNumber(), key -> new ArrayList<>()).add(result.getScore());
                }

                if (completed > 0) participantAverages.add(total / completed);
            }

            double avg = participantAverages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double highest = participantAverages.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            double lowest = participantAverages.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            int atRiskCount = (int) participantAverages.stream().filter(v -> v < passingScore).count();

            int excellent = (int) participantAverages.stream().filter(v -> v >= 75).count();
            int average = (int) participantAverages.stream().filter(v -> v >= 60 && v < 75).count();
            int poor = (int) participantAverages.stream().filter(v -> v < 60).count();

            List<Double> roundAverages = roundScores.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(entry -> entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0))
                    .toList();

            techData.add(new BatchAnalysisRequest.TechnologyData(
                    technology.getName(), bt.getTotalRounds(), participantAverages.size(),
                    avg, highest, lowest, atRiskCount, roundAverages,
                    new BatchAnalysisRequest.DistributionData(excellent, average, poor)
            ));
        }

        return new BatchAnalysisRequest(batchName, passingScore, techData);
    }

    private IndividualReportResponse fetchIndividualReportData(Long participantId) {
        ParticipantClient.ParticipantResponse participant = participantClient.getParticipantById(participantId).getData();
        if (participant == null) throw new RuntimeException("Participant not found");

        List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByParticipant(participantId).getData();
        List<IndividualReportResponse.RoundDetail> roundDetails = new ArrayList<>();
        double totalScore = 0;
        int completedRounds = 0;
        
        String batchName = "—";
        String techName = "—";
        int totalRounds = 0;

        if (enrollments != null && !enrollments.isEmpty()) {
            for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
                BatchClient.BatchTechnologyResponse bt = batchClient.getBatchTechnologyById(enrollment.getBatchTechnologyId()).getData();
                if (bt != null) {
                    BatchClient.BatchResponse batch = batchClient.getBatchById(bt.getBatchId()).getData();
                    BatchClient.TechnologyResponse tech = batchClient.getTechnologyById(bt.getTechnologyId()).getData();
                    
                    batchName = batch != null ? batch.getName() : "—";
                    techName = tech != null ? tech.getName() : "—";
                    totalRounds += bt.getTotalRounds();

                    List<EvaluationClient.AssignmentResponse> assignments = evaluationClient.getAssignmentsByEnrollment(enrollment.getId()).getData();
                    if (assignments != null) {
                        assignments.sort(Comparator.comparingInt(EvaluationClient.AssignmentResponse::getRoundNumber));
                        Integer prevScore = null;
                        for (EvaluationClient.AssignmentResponse assignment : assignments) {
                            UserClient.UserResponse evaluator = userClient.getUserById(assignment.getEvaluatorId()).getData();
                            
                            IndividualReportResponse.RoundDetail.RoundDetailBuilder roundBuilder = IndividualReportResponse.RoundDetail.builder()
                                    .roundNumber(assignment.getRoundNumber())
                                    .technology(tech != null ? tech.getName() : "Unknown")
                                    .evaluatorName(evaluator != null ? evaluator.getName() : "—")
                                    .status(assignment.getStatus());

                            if ("COMPLETED".equalsIgnoreCase(assignment.getStatus())) {
                                EvaluationClient.ResultResponse result = evaluationClient.getResultByAssignment(assignment.getId()).getData();
                                if (result != null) {
                                    roundBuilder.score(result.getScore())
                                            .submittedAt(LocalDateTime.now())
                                            .feedback(result.getStrengths())
                                            .previousScore(prevScore);
                                    
                                    totalScore += result.getScore();
                                    completedRounds++;
                                    prevScore = result.getScore();
                                }
                            } else {
                                roundBuilder.score(null).submittedAt(null).feedback("Pending");
                            }
                            roundDetails.add(roundBuilder.build());
                        }
                    }
                }
            }
        }

        double avg = completedRounds > 0 ? totalScore / completedRounds : 0.0;
        return IndividualReportResponse.builder()
                .participantId(participant.getId())
                .participantName(participant.getName())
                .participantEmail(participant.getEmail())
                .batchName(batchName)
                .technologyName(techName)
                .roundsCompleted(completedRounds)
                .totalRounds(totalRounds)
                .averageScore(avg)
                .technicalScore(avg)
                .communicationScore(Math.min(100, avg + 5))
                .problemSolvingScore(Math.min(100, avg - 2))
                .rounds(roundDetails)
                .build();
    }

    private BatchReportResponse fetchBatchReportData(Long batchId) {
        BatchClient.BatchResponse batch = batchClient.getBatchById(batchId).getData();
        if (batch == null) throw new RuntimeException("Batch not found");

        List<BatchClient.BatchTechnologyResponse> technologies = batchClient.getTechnologiesByBatchId(batchId).getData();
        List<BatchReportResponse.TechnologyGroup> techGroups = new ArrayList<>();
        
        int totalParticipants = 0;
        int completedCount = 0;
        int pendingCount = 0;

        if (technologies != null) {
            for (BatchClient.BatchTechnologyResponse bt : technologies) {
                BatchClient.TechnologyResponse tech = batchClient.getTechnologyById(bt.getTechnologyId()).getData();
                List<ParticipantClient.EnrollmentResponse> enrollments = participantClient.getEnrollmentsByBatchTechnology(bt.getId()).getData();
                
                List<BatchReportResponse.AssignmentRow> assignmentRows = new ArrayList<>();
                if (enrollments != null) {
                    totalParticipants += enrollments.size();
                    for (ParticipantClient.EnrollmentResponse enrollment : enrollments) {
                        ParticipantClient.ParticipantResponse participant = participantClient.getParticipantById(enrollment.getParticipantId()).getData();
                        List<EvaluationClient.AssignmentResponse> assignments = evaluationClient.getAssignmentsByEnrollment(enrollment.getId()).getData();
                        
                        if (assignments != null) {
                            for (EvaluationClient.AssignmentResponse assignment : assignments) {
                                UserClient.UserResponse evaluator = userClient.getUserById(assignment.getEvaluatorId()).getData();
                                
                                BatchReportResponse.AssignmentRow.AssignmentRowBuilder rowBuilder = BatchReportResponse.AssignmentRow.builder()
                                        .participantName(participant != null ? participant.getName() : "Unknown")
                                        .roundNumber(assignment.getRoundNumber())
                                        .evaluatorName(evaluator != null ? evaluator.getName() : "Not assigned")
                                        .status(assignment.getStatus());

                                if ("COMPLETED".equalsIgnoreCase(assignment.getStatus())) {
                                    EvaluationClient.ResultResponse result = evaluationClient.getResultByAssignment(assignment.getId()).getData();
                                    if (result != null) {
                                        rowBuilder.score(result.getScore())
                                                .submittedAt(LocalDateTime.now())
                                                .feedback(result.getStrengths());
                                        completedCount++;
                                    }
                                } else {
                                    rowBuilder.score(null).submittedAt(null).feedback("—");
                                    pendingCount++;
                                }
                                assignmentRows.add(rowBuilder.build());
                            }
                        }
                    }
                }
                
                techGroups.add(BatchReportResponse.TechnologyGroup.builder()
                        .technologyName(tech != null ? tech.getName() : "Unknown")
                        .totalRounds(bt.getTotalRounds())
                        .enrolledCount(enrollments != null ? enrollments.size() : 0)
                        .assignments(assignmentRows)
                        .build());
            }
        }

        return BatchReportResponse.builder()
                .batchName(batch.getName())
                .startDate(LocalDateTime.now().minusMonths(1))
                .endDate(LocalDateTime.now().plusMonths(1))
                .totalTechnologies(technologies != null ? technologies.size() : 0)
                .totalParticipants(totalParticipants)
                .completedCount(completedCount)
                .pendingCount(pendingCount)
                .technologies(techGroups)
                .build();
    }

    private ByteArrayInputStream generateIndividualPdf(IndividualReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30})).setWidth(UnitValue.createPercentValue(100));
            header.setBackgroundColor(PdfExportService.HEADER_DARK_COLOR);
            
            header.addCell(new Cell().add(new Paragraph("EvalTrack").setFontSize(22).setBold().setFontColor(ColorConstants.WHITE))
                    .add(new Paragraph("Individual Evaluation Report").setFontSize(10).setFontColor(new DeviceRgb(200, 200, 200)))
                    .setBorder(Border.NO_BORDER).setPadding(15));
            
            header.addCell(new Cell().add(new Paragraph("Generated: " + pdfExportService.formatDate(LocalDateTime.now())).setFontSize(8).setFontColor(new DeviceRgb(160, 160, 150)))
                    .add(new Paragraph("EvalTrack v1.0").setFontSize(8).setFontColor(new DeviceRgb(160, 160, 150)))
                    .setBorder(Border.NO_BORDER).setPadding(15));
            document.add(header);

            Table infoBar = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMarginTop(15);
            infoBar.addCell(new Cell().add(new Paragraph(data.getParticipantName()).setBold().setFontSize(16).setFontColor(PdfExportService.TEXT_PRIMARY))
                    .add(new Paragraph(data.getParticipantEmail()).setFontSize(10).setFontColor(PdfExportService.TEXT_SECONDARY))
                    .setBorder(Border.NO_BORDER));
            document.add(infoBar);

            Table chips = new Table(3).setWidth(UnitValue.createPercentValue(60)).setMarginTop(10);
            chips.addCell(pdfExportService.makeBadge(data.getBatchName(), PdfExportService.SECTION_BG, PdfExportService.TEXT_PRIMARY));
            chips.addCell(pdfExportService.makeBadge(data.getTechnologyName(), PdfExportService.SUCCESS_COLOR, ColorConstants.WHITE));
            chips.addCell(pdfExportService.makeBadge(data.getRoundsCompleted() + " Rounds Completed", PdfExportService.SUCCESS_COLOR, ColorConstants.WHITE));
            document.add(chips);

            document.add(new Paragraph("\nPerformance Summary").setBold().setFontSize(14).setMarginTop(20));
            Table cards = new Table(4).setWidth(UnitValue.createPercentValue(100)).setMarginTop(10);
            cards.addCell(makeScoreCard("Average Score", data.getAverageScore(), pdfExportService.getScoreColor(data.getAverageScore().intValue())));
            cards.addCell(makeScoreCard("Technical Score", data.getTechnicalScore(), new DeviceRgb(128, 0, 128)));
            cards.addCell(makeScoreCard("Communication", data.getCommunicationScore(), pdfExportService.getScoreColor(data.getCommunicationScore().intValue())));
            cards.addCell(makeScoreCard("Problem Solving", data.getProblemSolvingScore(), pdfExportService.getScoreColor(data.getProblemSolvingScore().intValue())));
            document.add(cards);

            document.add(new Paragraph("\n").setMarginTop(10));
            document.add(new Paragraph("Technical Knowledge: " + data.getTechnicalScore().intValue()).setFontSize(10));
            document.add(pdfExportService.createProgressBar(data.getTechnicalScore(), PdfExportService.ACCENT_COLOR).setMarginBottom(8));
            document.add(new Paragraph("Communication: " + data.getCommunicationScore().intValue()).setFontSize(10));
            document.add(pdfExportService.createProgressBar(data.getCommunicationScore(), PdfExportService.SUCCESS_COLOR).setMarginBottom(8));
            document.add(new Paragraph("Problem Solving: " + data.getProblemSolvingScore().intValue()).setFontSize(10));
            document.add(pdfExportService.createProgressBar(data.getProblemSolvingScore(), PdfExportService.WARNING_COLOR).setMarginBottom(8));

            document.add(new Paragraph("\nRound-wise Evaluation Details").setBold().setFontSize(14).setMarginTop(20));
            float[] columnWidths = {10, 15, 15, 18, 15, 27};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).setWidth(UnitValue.createPercentValue(100)).setMarginTop(10);

            String[] headers = {"Round", "Technology", "Score", "Evaluator", "Evaluated On", "Feedback"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h)).setBackgroundColor(PdfExportService.HEADER_DARK_COLOR).setFontColor(ColorConstants.WHITE).setBold().setPadding(8));
            }

            int rowIdx = 0;
            for (IndividualReportResponse.RoundDetail round : data.getRounds()) {
                com.itextpdf.kernel.colors.Color rowBg = rowIdx % 2 != 0 ? PdfExportService.SECTION_BG : ColorConstants.WHITE;
                
                table.addCell(new Cell().add(new Paragraph("Round " + round.getRoundNumber())).setBackgroundColor(rowBg).setPadding(8));
                table.addCell(new Cell().add(new Paragraph(round.getTechnology())).setBackgroundColor(rowBg).setPadding(8));
                
                if ("COMPLETED".equalsIgnoreCase(round.getStatus())) {
                    table.addCell(pdfExportService.makeScoreCell(round.getScore(), round.getPreviousScore()).setBackgroundColor(rowBg));
                    table.addCell(new Cell().add(new Paragraph(round.getEvaluatorName())).setBackgroundColor(rowBg).setPadding(8));
                    table.addCell(new Cell().add(new Paragraph(pdfExportService.formatDate(round.getSubmittedAt()))).setBackgroundColor(rowBg).setPadding(8));
                    
                    String feedback = round.getFeedback();
                    if (feedback != null && feedback.length() > 100) feedback = feedback.substring(0, 97) + "...";
                    table.addCell(new Cell().add(new Paragraph(feedback != null ? feedback : "—")).setBackgroundColor(rowBg).setPadding(8));
                } else {
                    table.addCell(new Cell().add(new Paragraph("—")).setBackgroundColor(rowBg).setPadding(8));
                    table.addCell(new Cell().add(new Paragraph(round.getEvaluatorName())).setBackgroundColor(rowBg).setPadding(8));
                    table.addCell(new Cell().add(new Paragraph("—")).setBackgroundColor(rowBg).setPadding(8));
                    table.addCell(pdfExportService.makeBadge("Pending", PdfExportService.WARNING_COLOR, ColorConstants.WHITE).setBackgroundColor(rowBg).setPadding(8));
                }
                rowIdx++;
            }
            document.add(table);

            document.add(new Paragraph("\n").setMarginTop(20));
            document.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)).setMarginBottom(5));
            Table footer = new Table(2).setWidth(UnitValue.createPercentValue(100));
            footer.addCell(new Cell().add(new Paragraph("EvalTrack — Mock Evaluation System")).setBorder(Border.NO_BORDER).setFontSize(8).setFontColor(PdfExportService.TEXT_SECONDARY));
            footer.addCell(new Cell().add(new Paragraph("Page 1 of 1")).setBorder(Border.NO_BORDER).setFontSize(8).setFontColor(PdfExportService.TEXT_SECONDARY).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateBatchPdf(BatchReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30})).setWidth(UnitValue.createPercentValue(100));
            header.setBackgroundColor(PdfExportService.HEADER_DARK_COLOR);
            header.addCell(new Cell().add(new Paragraph("EvalTrack").setFontSize(22).setBold().setFontColor(ColorConstants.WHITE))
                    .add(new Paragraph("Batch Evaluation Report").setFontSize(10).setFontColor(new DeviceRgb(200, 200, 200)))
                    .setBorder(Border.NO_BORDER).setPadding(15));
            header.addCell(new Cell().add(new Paragraph("Generated: " + pdfExportService.formatDate(LocalDateTime.now())).setFontSize(8).setFontColor(new DeviceRgb(160, 160, 150)))
                    .add(new Paragraph("EvalTrack v1.0").setFontSize(8).setFontColor(new DeviceRgb(160, 160, 150)))
                    .setBorder(Border.NO_BORDER).setPadding(15));
            document.add(header);

            Table infoBar = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMarginTop(15);
            infoBar.addCell(new Cell().add(new Paragraph(data.getBatchName()).setBold().setFontSize(16).setFontColor(PdfExportService.TEXT_PRIMARY))
                    .add(new Paragraph(pdfExportService.formatDate(data.getStartDate()) + " — " + pdfExportService.formatDate(data.getEndDate())).setFontSize(10).setFontColor(PdfExportService.TEXT_SECONDARY))
                    .setBorder(Border.NO_BORDER));
            document.add(infoBar);

            Table chips = new Table(3).setWidth(UnitValue.createPercentValue(65)).setMarginTop(10);
            chips.addCell(pdfExportService.makeBadge(data.getTotalTechnologies() + " Technologies", PdfExportService.SECTION_BG, PdfExportService.TEXT_PRIMARY));
            chips.addCell(pdfExportService.makeBadge(data.getTotalParticipants() + " Participants", PdfExportService.ACCENT_COLOR, ColorConstants.WHITE));
            chips.addCell(pdfExportService.makeBadge(data.getCompletedCount() + " Completed · " + data.getPendingCount() + " Pending", PdfExportService.SUCCESS_COLOR, ColorConstants.WHITE));
            document.add(chips);

            for (BatchReportResponse.TechnologyGroup tech : data.getTechnologies()) {
                document.add(new Paragraph("\n").setMarginTop(15));
                Table techHeader = new Table(2).setWidth(UnitValue.createPercentValue(100)).setBackgroundColor(PdfExportService.SECTION_BG).setPadding(10);
                techHeader.addCell(new Cell().add(new Paragraph(tech.getTechnologyName()).setBold().setFontSize(12)).setBorder(Border.NO_BORDER).setPadding(8));
                techHeader.addCell(new Cell().add(new Paragraph(tech.getTotalRounds() + " rounds configured · " + tech.getEnrolledCount() + " participants")).setBorder(Border.NO_BORDER).setPadding(8).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT).setFontSize(10));
                document.add(techHeader);

                if (tech.getAssignments() == null || tech.getAssignments().isEmpty()) {
                    document.add(new Paragraph("No participants enrolled in " + tech.getTechnologyName() + " yet.").setFontColor(PdfExportService.TEXT_SECONDARY).setItalic().setMarginLeft(10));
                } else {
                    float[] columnWidths = {16, 8, 12, 16, 14, 10, 24};
                    Table table = new Table(UnitValue.createPercentArray(columnWidths)).setWidth(UnitValue.createPercentValue(100)).setMarginTop(5);
                    String[] headers = {"Participant", "Round", "Score", "Evaluator", "Evaluated On", "Status", "Feedback"};
                    for (String h : headers) {
                        table.addHeaderCell(new Cell().add(new Paragraph(h)).setBackgroundColor(PdfExportService.HEADER_DARK_COLOR).setFontColor(ColorConstants.WHITE).setBold().setFontSize(9).setPadding(5));
                    }

                    int rowIdx = 0;
                    for (BatchReportResponse.AssignmentRow row : tech.getAssignments()) {
                        com.itextpdf.kernel.colors.Color rowBg = rowIdx % 2 != 0 ? PdfExportService.SECTION_BG : ColorConstants.WHITE;
                        table.addCell(new Cell().add(new Paragraph(row.getParticipantName()).setBold()).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                        table.addCell(new Cell().add(new Paragraph("R" + row.getRoundNumber())).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                        
                        if ("COMPLETED".equalsIgnoreCase(row.getStatus())) {
                            table.addCell(new Cell().add(new Paragraph(row.getScore() + " / 100").setFontColor(pdfExportService.getScoreColor(row.getScore()))).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(new Cell().add(new Paragraph(row.getEvaluatorName())).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(new Cell().add(new Paragraph(pdfExportService.formatDate(row.getSubmittedAt()))).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(pdfExportService.makeBadge("Completed", PdfExportService.SUCCESS_COLOR, ColorConstants.WHITE).setBackgroundColor(rowBg).setFontSize(8).setPadding(5));
                            
                            String feedback = row.getFeedback();
                            if (feedback != null && feedback.length() > 80) feedback = feedback.substring(0, 77) + "...";
                            table.addCell(new Cell().add(new Paragraph(feedback != null ? feedback : "—")).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                        } else {
                            table.addCell(new Cell().add(new Paragraph("—")).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(new Cell().add(new Paragraph(row.getEvaluatorName())).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(new Cell().add(new Paragraph("—")).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                            table.addCell(pdfExportService.makeBadge("Pending", PdfExportService.WARNING_COLOR, ColorConstants.WHITE).setBackgroundColor(rowBg).setFontSize(8).setPadding(5));
                            table.addCell(new Cell().add(new Paragraph("—")).setBackgroundColor(rowBg).setFontSize(9).setPadding(5));
                        }
                        rowIdx++;
                    }
                    document.add(table);
                }
            }

            document.add(new Paragraph("\n").setMarginTop(20));
            document.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1f)).setMarginBottom(5));
            Table footer = new Table(2).setWidth(UnitValue.createPercentValue(100));
            footer.addCell(new Cell().add(new Paragraph("EvalTrack — Mock Evaluation System")).setBorder(Border.NO_BORDER).setFontSize(8).setFontColor(PdfExportService.TEXT_SECONDARY));
            footer.addCell(new Cell().add(new Paragraph("Page 1 of 1")).setBorder(Border.NO_BORDER).setFontSize(8).setFontColor(PdfExportService.TEXT_SECONDARY).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private Cell makeScoreCard(String title, Double score, com.itextpdf.kernel.colors.Color color) {
        return new Cell().add(new Paragraph(title).setFontSize(9).setFontColor(PdfExportService.TEXT_SECONDARY))
                .add(new Paragraph(String.format("%.1f", score)).setFontSize(16).setBold().setFontColor(color))
                .setBorder(new SolidBorder(PdfExportService.BORDER_COLOR, 1))
                .setPadding(10).setBackgroundColor(ColorConstants.WHITE);
    }

    private ByteArrayInputStream generatePdf(ReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String title = data.getBatchName() != null 
                ? "Batch Evaluation Report: " + data.getBatchName() 
                : "Individual Evaluation Report: " + data.getParticipantName();
            document.add(new Paragraph(title).setFontSize(18).setBold());

            for (ReportResponse.TechnologyReport tr : data.getTechnologies()) {
                document.add(new Paragraph("\nTechnology: " + tr.getTechnologyName()).setBold().setFontSize(14));
                
                Table table = new Table(4);
                table.setWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell("Participant");
                table.addHeaderCell("Average Score");
                table.addHeaderCell("Rounds");
                table.addHeaderCell("Round Feedback");

                for (ReportResponse.ParticipantScore ps : tr.getParticipantScores()) {
                    String feedback = ps.getRounds() == null ? "No data" : ps.getRounds().stream()
                            .map(r -> "R" + r.getRoundNumber() + ": " + r.getFeedback())
                            .collect(Collectors.joining("\n"));

                    table.addCell(ps.getParticipantName());
                    table.addCell(String.format("%.2f", ps.getAverageScore()));
                    table.addCell(String.valueOf(ps.getRounds().size()));
                    table.addCell(feedback);
                }
                document.add(table);
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateCsv(ReportResponse data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(
                "Entity", "Technology", "Participant", "Average Score", "Rounds", "Feedback"
        ))) {
            String entityName = data.getBatchName() != null ? data.getBatchName() : data.getParticipantName();
            for (ReportResponse.TechnologyReport tr : data.getTechnologies()) {
                for (ReportResponse.ParticipantScore ps : tr.getParticipantScores()) {
                    String feedback = ps.getRounds() == null ? "" : ps.getRounds().stream()
                            .map(r -> "R" + r.getRoundNumber() + ": " + r.getFeedback())
                            .collect(Collectors.joining(" | "));

                    csvPrinter.printRecord(
                            entityName,
                            tr.getTechnologyName(),
                            ps.getParticipantName(),
                            String.format("%.2f", ps.getAverageScore()),
                            ps.getRounds().size(),
                            feedback
                    );
                }
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void emailParticipantReport(Long participantId) {
        IndividualReportResponse reportData = fetchIndividualReportData(participantId);
        
        // Generate PDF and save to temp file
        String tempFilePath = generateParticipantPdfToFile(reportData);
        
        // Build event
        ParticipantReportEmailEvent event = ParticipantReportEmailEvent.builder()
                .participantId(participantId)
                .participantName(reportData.getParticipantName())
                .participantEmail(reportData.getParticipantEmail())
                .batchName(reportData.getBatchName())
                .technologyName(reportData.getTechnologyName())
                .pdfFilePath(tempFilePath)
                .build();
        
        // Publish event
        participantReportEmailProducer.publishParticipantReportEmailEvent(event);
    }

    private String generateParticipantPdfToFile(IndividualReportResponse data) {
        String fileName = "report_" + data.getParticipantId() + "_" + System.currentTimeMillis() + ".pdf";
        File reportsDir = new File("/app/temp_reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        File tempFile = new File(reportsDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            ByteArrayInputStream bis = generateIndividualPdf(data);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            log.info("[REPORT-GENERATED] Participant PDF report generated and saved to: {}", tempFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save PDF to temp file", e);
            throw new RuntimeException("Failed to generate PDF for email");
        }
        
        return tempFile.getAbsolutePath();
    }
}
