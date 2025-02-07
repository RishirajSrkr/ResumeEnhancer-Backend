package com.rishiraj.syncResume.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ExtractMessage {

    public String extractMessage(String jsonResponse) {
        try {
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Navigate to the content field
            JsonNode messageNode = rootNode
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text");

            // Return the message content as a String
            String text = messageNode.asText();
            int start = 0;
            int end = text.length();
            for(int i = 0; i <text.length(); i++){
                char ch = text.charAt(i);
                if(ch == '{') break;
                else start ++;
            }
            for(int i = text.length()-1; i >= 0; i--){
                char ch = text.charAt(i);
                if(ch == '}') break;
                else end --;
            }

            return text.substring(start, end);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract message from response", e);
        }
    }

}
