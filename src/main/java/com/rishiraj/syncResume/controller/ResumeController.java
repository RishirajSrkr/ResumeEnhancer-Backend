package com.rishiraj.syncResume.controller;

import com.rishiraj.syncResume.services.ApiService;
import com.rishiraj.syncResume.services.ResumeServices;
import com.rishiraj.syncResume.utilities.ExtractMessage;
import jdk.jfr.ContentType;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.HTML;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/resume")
public class ResumeController {
    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);
    @Autowired
    private ResumeServices resumeServices;

    @Autowired
    private ApiService apiService;

    @Autowired
    private ExtractMessage extractMessage;


    @GetMapping("/test-llm")
    public ResponseEntity<String> testLLM() {
        try {
            // ✅ Now pass 2 parameters: systemPrompt and userMessage
            String response = apiService.sendMessage(
                    "You are a helpful assistant.",
                    "Say hello in JSON format"
            );

            String extracted = extractMessage.extractMessage(response);
            return ResponseEntity.ok(extracted);
        } catch (Exception e) {
            log.error("Test failed", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile resumeFile,
            @RequestPart("jd") String jobDescription
    ) {
        try {
            log.info("Processing resume upload...");

            // ✅ This already returns extracted JSON
            String enhancedJson = resumeServices.generateEnhancedResumeText(jobDescription, resumeFile);

            log.info("Enhanced JSON: {}", enhancedJson);

            // ✅ Return directly - no second extraction needed
            return ResponseEntity.ok(enhancedJson);

        } catch (IllegalArgumentException e) {
            log.error("Bad request error", e);
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());

        } catch (Exception e) {
            log.error("Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing resume: " + e.getMessage());
        }
    }
}
