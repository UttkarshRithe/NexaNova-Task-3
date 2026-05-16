package com.techtraining.ai.service.impl;

import com.techtraining.ai.dto.request.FeedbackRequest;
import com.techtraining.ai.dto.response.FeedbackResponse;
import com.techtraining.ai.service.FeedbackGenerationService;
import com.techtraining.common.exception.ServiceCommunicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FeedbackGenerationServiceImpl implements FeedbackGenerationService {

    private final WebClient webClient;

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.api-url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    public FeedbackGenerationServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public FeedbackResponse generateFeedback(FeedbackRequest request) {
        String prompt = buildFeedbackPrompt(request);
        log.info("Starting OpenRouter DeepSeek feedback generation. Tech: {}, Round: {}", request.getTechnology(), request.getRoundNumber());

        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                )
            );

            log.debug("OpenRouter Request Body: {}", requestBody);

            Map<String, Object> response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://nexanova.ai") // OpenRouter likes a referer
                    .header("X-Title", "NexaNova")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("OpenRouter API Error: Status={}, Body={}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new ServiceCommunicationException("OpenRouter API returned error: " + clientResponse.statusCode()));
                                });
                    })
                    .bodyToMono(Map.class)
                    .block();

            log.info("OpenRouter Raw Response: {}", response);

            String aiFeedback = parseResponse(response);
            if (aiFeedback == null || aiFeedback.isBlank()) {
                log.warn("OpenRouter returned empty or null content. Checking for errors in response object.");
                if (response != null && response.containsKey("error")) {
                    log.error("OpenRouter Application Error: {}", response.get("error"));
                }
                throw new ServiceCommunicationException("Empty response from AI provider");
            }

            log.info("Successfully received AI feedback ({} chars)", aiFeedback.length());

            return FeedbackResponse.builder()
                    .aiFeedback(aiFeedback.trim())
                    .performanceLevel(determinePerformanceLevel(request.getOverallScore()))
                    .build();

        } catch (Exception e) {
            log.error("AI feedback generation failed: {}", e.getMessage(), e);
            log.info("Triggering fallback logic for feedback generation");
            return FeedbackResponse.builder()
                    .aiFeedback("The participant demonstrated good foundational skills, but there are areas for improvement. Focus on refining core concepts and practicing real-world scenarios. With continued effort, the performance can be significantly enhanced.")
                    .performanceLevel(determinePerformanceLevel(request.getOverallScore()))
                    .build();
        }
    }

    private String parseResponse(Map<String, Object> response) {
        try {
            if (response == null || !response.containsKey("choices")) return null;
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) return null;
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || !message.containsKey("content")) return null;
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("Error parsing OpenRouter response JSON", e);
            return null;
        }
    }

    private String buildFeedbackPrompt(FeedbackRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert technical evaluator writing professional feedback ");
        prompt.append("for a training evaluation system.\n\n");

        prompt.append("EVALUATION CONTEXT:\n");
        prompt.append("Technology: ").append(request.getTechnology()).append("\n");
        prompt.append("Round: ").append(request.getRoundNumber()).append("\n");
        prompt.append("Overall Score: ").append(request.getOverallScore()).append("/100\n");
        prompt.append("Difficulty Level: ").append(
                request.getDifficultyLevel() != null ? request.getDifficultyLevel() : "MEDIUM"
        ).append("\n\n");

        if (request.getTechnicalScore() != null) {
            prompt.append("RUBRIC SCORES:\n");
            prompt.append("- Technical Knowledge: ").append(request.getTechnicalScore()).append("/100\n");
            prompt.append("- Communication: ").append(request.getCommunicationScore()).append("/100\n");
            prompt.append("- Problem Solving: ").append(request.getProblemSolvingScore()).append("/100\n\n");
        }

        if (request.getStrengths() != null && !request.getStrengths().isBlank()) {
            prompt.append("EVALUATOR OBSERVED STRENGTHS:\n");
            prompt.append(request.getStrengths()).append("\n\n");
        }

        if (request.getWeaknesses() != null && !request.getWeaknesses().isBlank()) {
            prompt.append("EVALUATOR OBSERVED WEAKNESSES:\n");
            prompt.append(request.getWeaknesses()).append("\n\n");
        }

        prompt.append("EVALUATOR COMMENT:\n");
        prompt.append(request.getEvaluatorComment()).append("\n\n");

        prompt.append("INSTRUCTIONS:\n");
        prompt.append("Write a professional 3-paragraph feedback report for this participant.\n");
        prompt.append("Paragraph 1: Acknowledge what the participant did well.\n");
        prompt.append("Paragraph 2: Clearly explain areas needing improvement.\n");
        prompt.append("Paragraph 3: Give specific actionable advice for improvement.\n");
        prompt.append("Tone: Professional, constructive, encouraging.\n");
        prompt.append("Length: 100-150 words total.\n");
        prompt.append("Do NOT use participant name. Do NOT use bullet points. ");
        prompt.append("Write in continuous paragraph form only.");

        return prompt.toString();
    }

    private String determinePerformanceLevel(Integer score) {
        if (score == null) return "AVERAGE";
        if (score >= 85) return "EXCELLENT";
        if (score >= 70) return "GOOD";
        if (score >= 60) return "AVERAGE";
        return "POOR";
    }
}
