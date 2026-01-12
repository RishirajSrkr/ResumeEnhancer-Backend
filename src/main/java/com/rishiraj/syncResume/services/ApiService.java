package com.rishiraj.syncResume.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WebClient webClient;

    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.groq.com")
                .build();
    }

    public String sendMessage(String systemPrompt, String userMessage) {

        // ✅ Debug: Log request size
        int systemPromptLength = systemPrompt.length();
        int userMessageLength = userMessage.length();
        int totalLength = systemPromptLength + userMessageLength;

        log.info("System Prompt Length: {} chars", systemPromptLength);
        log.info("User Message Length: {} chars", userMessageLength);
        log.info("Total Request Length: {} chars", totalLength);

        if (totalLength > 8000) {
            log.warn("⚠️ Request might be too large! Consider reducing input size.");
        }
        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.3,
                "max_tokens", 10000  // ✅ Increased from 8000 to allow detailed responses
        );

        try {
            String response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("❌ Groq API Error Response: {}", errorBody);
                                        return Mono.error(new RuntimeException("Groq API Error: " + errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            return response;

        } catch (
                Exception e) {
            log.error("❌ Failed to call Groq API", e);
            throw new RuntimeException("Failed to call Groq API: " + e.getMessage(), e);
        }
    }
}