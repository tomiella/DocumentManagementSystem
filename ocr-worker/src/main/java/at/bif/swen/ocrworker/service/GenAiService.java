package at.bif.swen.ocrworker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GenAiService {
    private static final Logger log = LoggerFactory.getLogger(GenAiService.class);
    private final RestClient restClient;
    private final String apiKey;

    public GenAiService(@Value("${GEMINI_API_KEY:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                .build();
    }

    public String summarize(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY is not set, skipping summarization.");
            return null;
        }

        try {
            var requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text",
                                            "Please summarize the following text in a concise manner:\n\n" + text)))));

            var response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(Map.class);

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                // Parse the deeply nested response safely
                if (body.get("candidates") instanceof List<?> candidates && !candidates.isEmpty()) {
                    if (candidates.get(0) instanceof Map<?, ?> candidate) {
                        if (candidate.get("content") instanceof Map<?, ?> content) {
                            if (content.get("parts") instanceof List<?> parts && !parts.isEmpty()) {
                                if (parts.get(0) instanceof Map<?, ?> part) {
                                    return (String) part.get("text");
                                }
                            }
                        }
                    }
                }
            }

            return null;

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return null;
        }
    }
}
