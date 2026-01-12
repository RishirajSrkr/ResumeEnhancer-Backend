package com.rishiraj.syncResume.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ExtractMessage {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractMessage(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // ✅ Groq / OpenAI compatible path
            String content = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // ✅ Remove markdown fences if present
            content = content
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // ✅ Extract pure JSON safely
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');

            if (start == -1 || end == -1 || start >= end) {
                throw new RuntimeException("No valid JSON found in LLM response");
            }

            return content.substring(start, end + 1);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract message from Groq response", e);
        }
    }
}
