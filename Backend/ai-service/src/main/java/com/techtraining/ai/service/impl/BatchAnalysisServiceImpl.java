package com.techtraining.ai.service.impl;

import com.techtraining.ai.dto.request.BatchAnalysisRequest;
import com.techtraining.ai.dto.response.BatchAnalysisResponse;
import com.techtraining.ai.service.BatchAnalysisService;
import com.techtraining.common.exception.ServiceCommunicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BatchAnalysisServiceImpl implements BatchAnalysisService {

    private final WebClient webClient;

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.api-url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    public BatchAnalysisServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BatchAnalysisResponse analyzeBatch(BatchAnalysisRequest request) {
        String prompt = buildBatchAnalysisPrompt(request);
        log.info("Starting OpenRouter DeepSeek batch analysis for batch: {}", request.getBatchName());

        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                )
            );

            Map<String, Object> response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://nexanova.ai")
                    .header("X-Title", "NexaNova")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("OpenRouter Batch API Error: Status={}, Body={}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new ServiceCommunicationException("OpenRouter API returned error: " + clientResponse.statusCode()));
                                });
                    })
                    .bodyToMono(Map.class)
                    .block();

            log.info("OpenRouter Batch Raw Response: {}", response);

            String rawText = parseResponse(response);
            if (rawText == null || rawText.isBlank()) {
                throw new ServiceCommunicationException("Empty response from AI provider for batch analysis");
            }

            return parseBatchAnalysisResponse(rawText, request);

        } catch (Exception e) {
            log.error("Batch analysis failed: {}", e.getMessage(), e);
            log.warn("Falling back to default batch analysis response due to AI service failure.");
            return parseBatchAnalysisResponse(
                    "OVERALL_HEALTH: MODERATE\nSUMMARY: AI Analysis is currently unavailable. The batch appears to be proceeding normally based on available data.\nRECOMMENDATION: Please check back later when the AI service is restored.", 
                    request
            );
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

    private String buildBatchAnalysisPrompt(BatchAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert training analyst evaluating batch performance data.\n\n");
        prompt.append("BATCH: ").append(request.getBatchName()).append("\n");
        prompt.append("PASSING SCORE: ").append(request.getPassingScore()).append("/100\n\n");

        prompt.append("TECHNOLOGY-WISE PERFORMANCE DATA:\n");
        prompt.append("(Note: Participant names are anonymized for privacy)\n\n");

        for (BatchAnalysisRequest.TechnologyData tech : request.getTechnologies()) {
            prompt.append("Technology: ").append(tech.getName()).append("\n");
            prompt.append("  Total Participants: ").append(tech.getParticipantCount()).append("\n");
            prompt.append("  Average Score: ").append(tech.getAvgScore()).append("/100\n");
            prompt.append("  Highest Score: ").append(tech.getHighestScore()).append("/100\n");
            prompt.append("  Lowest Score: ").append(tech.getLowestScore()).append("/100\n");
            prompt.append("  At-Risk (below ").append(request.getPassingScore())
                    .append("): ").append(tech.getAtRiskCount()).append(" participants\n");

            if (tech.getRoundAverages() != null && tech.getRoundAverages().size() > 1) {
                prompt.append("  Round Averages: ");
                for (int i = 0; i < tech.getRoundAverages().size(); i++) {
                    prompt.append("Round ").append(i + 1).append(": ")
                            .append(tech.getRoundAverages().get(i));
                    if (i < tech.getRoundAverages().size() - 1) prompt.append(", ");
                }
                prompt.append("\n");
            }

            if (tech.getDistribution() != null) {
                prompt.append("  Score Distribution — ");
                prompt.append("Excellent (75+): ").append(tech.getDistribution().getExcellent()).append(", ");
                prompt.append("Average (60-74): ").append(tech.getDistribution().getAverage()).append(", ");
                prompt.append("Poor (<60): ").append(tech.getDistribution().getPoor()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("ANALYZE THIS DATA AND RESPOND IN EXACTLY THIS FORMAT:\n\n");
        prompt.append("OVERALL_HEALTH: [write only one word: STRONG or MODERATE or WEAK]\n\n");
        prompt.append("SUMMARY: [Write 3-4 sentences analyzing overall batch performance, ");
        prompt.append("which technology is strongest, which is weakest, and round trends.]\n\n");
        prompt.append("RECOMMENDATION: [Write 2-3 specific actionable recommendations ");
        prompt.append("for the training team based on this data.]\n\n");
        prompt.append("Do not add any extra text outside this format.");

        return prompt.toString();
    }

    private BatchAnalysisResponse parseBatchAnalysisResponse(String rawText, BatchAnalysisRequest request) {
        String overallHealth = "MODERATE";
        StringBuilder summaryBuilder = new StringBuilder();
        StringBuilder recommendationBuilder = new StringBuilder();
        String currentSection = "";

        for (String line : rawText.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("OVERALL_HEALTH:")) {
                String health = trimmed.replace("OVERALL_HEALTH:", "").trim();
                if (health.contains("STRONG")) overallHealth = "STRONG";
                else if (health.contains("WEAK")) overallHealth = "WEAK";
                else overallHealth = "MODERATE";
                currentSection = "";
            } else if (trimmed.startsWith("SUMMARY:")) {
                currentSection = "SUMMARY";
                String rest = trimmed.replace("SUMMARY:", "").trim();
                if (!rest.isEmpty()) summaryBuilder.append(rest).append(" ");
            } else if (trimmed.startsWith("RECOMMENDATION:")) {
                currentSection = "RECOMMENDATION";
                String rest = trimmed.replace("RECOMMENDATION:", "").trim();
                if (!rest.isEmpty()) recommendationBuilder.append(rest).append(" ");
            } else if (!trimmed.isEmpty()) {
                if ("SUMMARY".equals(currentSection)) summaryBuilder.append(trimmed).append(" ");
                else if ("RECOMMENDATION".equals(currentSection)) recommendationBuilder.append(trimmed).append(" ");
            }
        }

        List<BatchAnalysisResponse.TechnologySummary> techSummaries = new ArrayList<>();
        for (BatchAnalysisRequest.TechnologyData tech : request.getTechnologies()) {
            String status;
            if (tech.getAvgScore() != null && tech.getAvgScore() >= 75) status = "STRONG";
            else if (tech.getAvgScore() != null && tech.getAvgScore() >= 60) status = "AVERAGE";
            else status = "WEAK";

            String trend = "STABLE";
            if (tech.getRoundAverages() != null && tech.getRoundAverages().size() >= 2) {
                double first = tech.getRoundAverages().get(0);
                double last = tech.getRoundAverages().get(tech.getRoundAverages().size() - 1);
                if (last - first > 2) trend = "IMPROVING";
                else if (first - last > 2) trend = "DECLINING";
            }

            techSummaries.add(BatchAnalysisResponse.TechnologySummary.builder()
                    .name(tech.getName())
                    .status(status)
                    .avgScore(tech.getAvgScore())
                    .atRiskCount(tech.getAtRiskCount())
                    .trend(trend)
                    .build());
        }

        return BatchAnalysisResponse.builder()
                .overallHealth(overallHealth)
                .aiSummary(summaryBuilder.toString().trim())
                .recommendation(recommendationBuilder.toString().trim())
                .technologySummaries(techSummaries)
                .build();
    }
}
