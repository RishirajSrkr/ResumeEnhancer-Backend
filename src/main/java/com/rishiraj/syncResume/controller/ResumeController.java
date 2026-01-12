package com.rishiraj.syncResume.controller;

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
@CrossOrigin("*")
@RequestMapping("/api/resume")
public class ResumeController {
    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);
    @Autowired
    private ResumeServices resumeServices;

    @Autowired
    private ExtractMessage extractMessage;


    @GetMapping("/check")
    public ResponseEntity<String> check(){
        return new ResponseEntity<>("Successful", HttpStatus.OK);
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile resumeFile, @RequestPart("jd") String jobDescription) {
        try {
            log.info("I am here");
            String jsonResponse = resumeServices.generateEnhancedResumeText(jobDescription, resumeFile);
            log.info(jsonResponse);

            String formattedResponse = extractMessage.extractMessage(jsonResponse);
            log.info(formattedResponse);
            return new ResponseEntity<>(formattedResponse, HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (
                Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
