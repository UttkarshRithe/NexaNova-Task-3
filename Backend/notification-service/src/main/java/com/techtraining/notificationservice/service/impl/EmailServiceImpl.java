package com.techtraining.notificationservice.service.impl;

import com.techtraining.common.event.EvaluationAssignedEvent;
import com.techtraining.common.event.ParticipantReportEmailEvent;
import com.techtraining.notificationservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendAssignmentEmail(EvaluationAssignedEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("participantName", event.getParticipantName());
            context.setVariable("batchName", event.getBatchName());
            context.setVariable("technologyName", event.getTechnologyName());
            context.setVariable("roundNumber", event.getRoundNumber());
            context.setVariable("evaluatorName", event.getEvaluatorName());
            context.setVariable("evaluationDate", event.getEvaluationDate());
            context.setVariable("evaluationTime", event.getEvaluationTime());
            context.setVariable("meetingLink", event.getMeetingLink());

            String htmlContent = templateEngine.process("evaluation-assignment-email", context);

            helper.setTo(event.getParticipantEmail());
            helper.setSubject("Mock Evaluation Scheduled — EvalTrack");
            helper.setText(htmlContent, true);
            helper.setFrom("yashrithe443@gmail.com");

            log.info("[EMAIL-SENDING] Sending email to: {}", event.getParticipantEmail());
            mailSender.send(message);
            log.info("[EMAIL-SENT] Email successfully sent to: {}", event.getParticipantEmail());
        } catch (Exception e) {
            log.error("Error while sending email to {}: {}", event.getParticipantEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    @Override
    public void sendParticipantReport(ParticipantReportEmailEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("participantName", event.getParticipantName());
            context.setVariable("batchName", event.getBatchName());
            context.setVariable("technologyName", event.getTechnologyName());

            String htmlContent = templateEngine.process("participant-report-email", context);

            helper.setTo(event.getParticipantEmail());
            helper.setSubject("Your Evaluation Report — EvalTrack");
            helper.setText(htmlContent, true);
            helper.setFrom("yashrithe443@gmail.com");

            // Attach PDF
            File pdfFile = new File(event.getPdfFilePath());
            if (pdfFile.exists()) {
                helper.addAttachment(event.getParticipantName().replace(" ", "_") + "_Evaluation_Report.pdf", pdfFile);
            } else {
                log.error("PDF file not found at: {}", event.getPdfFilePath());
            }

            log.info("[EMAIL-SENDING] Sending report email to: {}", event.getParticipantEmail());
            mailSender.send(message);
            log.info("[EMAIL-SENT] Report email successfully sent to: {}", event.getParticipantEmail());

            // Delete temp file
            if (pdfFile.exists()) {
                boolean deleted = pdfFile.delete();
                log.info("[FILE-DELETED] Temp PDF file deleted: {} - Status: {}", event.getPdfFilePath(), deleted);
            }
        } catch (Exception e) {
            log.error("[EMAIL-FAILED] Failed to send participant report email to {}: {}", 
                      event.getParticipantEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
