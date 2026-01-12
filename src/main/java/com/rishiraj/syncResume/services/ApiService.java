package com.rishiraj.syncResume.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    
    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WebClient webClient;

    public ApiService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    public String sendMessage(String prompt) throws JsonProcessingException {

        Map<String, Object> requestBody = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", new Object[]{
                Map.of("role", "user", "content", prompt)
            }
        );

        String response = webClient.post()
                .uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}