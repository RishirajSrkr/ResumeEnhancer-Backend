package com.rishiraj.syncResume.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rishiraj.syncResume.utilities.ExtractMessage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeServices {

    private static final Logger log = LoggerFactory.getLogger(ResumeServices.class);

    @Autowired
    private ApiService apiService;

    @Autowired
    private ExtractMessage extractMessage;

    public String generateEnhancedResumeText(String jobDescription, MultipartFile resumeFile) throws Exception {
        String extractedTextFromResume = extractTextFromResume(resumeFile);
        return enhanceWithLLM(extractedTextFromResume, jobDescription);
    }


    public String extractTextFromResume(MultipartFile resumeFile) throws Exception {
        String fileName = resumeFile.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalAccessException("Invalid File.");
        }

        String resumeText = "";

        if (fileName.endsWith(".pdf")) {
            resumeText = extractTextFromPDF(resumeFile);

        } else if (fileName.endsWith(".docx")) {
            resumeText = extractTextFromDOCX(resumeFile);

        }
        return resumeText;
    }


    private String enhanceWithLLM(String extractedTextFromResume, String jobDescription) throws JsonProcessingException {

        // ✅ Enhanced system prompt with better instructions
        String systemPrompt = """
        You are an expert ATS Resume Optimizer. Your goal is to enhance the resume to match the job description while preserving ALL technical details, metrics, and achievements.
        
        CRITICAL RULES:
        1. PRESERVE all specific technical details (technologies, tools, frameworks)
        2. PRESERVE all quantifiable metrics and numbers
        3. PRESERVE all project features and functionality descriptions
        4. DO NOT make content generic - keep specific implementation details
        5. ADD relevant keywords from job description naturally
        6. Enhance descriptions by highlighting aspects relevant to the target role
        
        SKILLS (max 20 in 4-5 categories):
        - Extract ALL technologies mentioned in resume
        - Add relevant tools from job description if they match experience
        - Categorize logically (Languages, Backend, Frontend, DevOps, etc)
        
        PROJECTS (max 4):
        - Keep ALL technical stack details
        - Preserve specific features (e.g., "JWT role-based auth", "Redis caching", "Razorpay integration")
        - Maintain implementation details (e.g., "Dockerized backend", "AWS S3 for images")
        - Description: 40-50 words with technical depth
        
        WORK EXPERIENCE:
        - If present, keep ALL specific technologies and methodologies
        - Preserve metrics and impact (e.g., "optimized using Redis")
        - Each responsibility: 15-20 words with technical details
        
        OBJECTIVE:
        - Use target role title from job description
        - Mention top 3 relevant skills from resume
        - 35-40 words
        
        ACHIEVEMENTS:
        - Extract from projects and experience
        - Include specific technical accomplishments
        - Mention tools, scale, and impact
        - 4-5 achievements
        
        RESPONSE FORMAT: Return ONLY valid JSON, no markdown, no explanations.
        
        JSON Structure:
        {
          "name": "string",
          "contact": {"email": "string", "phone": "string|null", "github": "string|null", "linkedin": "string|null"},
          "objective": "string (35-40 words)",
          "skillsByCategory": {
            "Programming Languages": ["string"],
            "Backend Technologies": ["string"],
            "Frontend Technologies": ["string"],
            "Databases & Caching": ["string"],
            "DevOps & Cloud": ["string"]
          },
          "projects": [
            {
              "title": "string",
              "description": "string (40-50 words with technical details)",
              "tech_stack": ["string"],
              "link": "string|null",
              "githubLink": "string|null"
            }
          ],
          "workExperience": [
            {
              "title": "string",
              "company": "string",
              "duration": "string|null",
              "responsibilities": ["string (15-20 words each)"]
            }
          ],
          "education": {
            "degree": "string",
            "branch": "string|null",
            "institution": "string",
            "year": "string|null",
            "percentage": 0,
            "cgpa": 0
          },
          "achievements": ["string (specific technical accomplishments)"],
          "certificates": ["string"]
        }
        """;

        String trimmedJD = jobDescription.length() > 3000 ? jobDescription.substring(0, 3000) : jobDescription;
        String trimmedResume = extractedTextFromResume.length() > 5000 ? extractedTextFromResume.substring(0, 5000) : extractedTextFromResume;

        String userMessage = String.format("""
        JOB DESCRIPTION:
        %s
        
        ORIGINAL RESUME:
        %s
        
        TASK: Enhance this resume to match the job description while preserving ALL technical details, specific features, and implementation information. Do not make descriptions generic.
        """, trimmedJD, trimmedResume);

        // Call API
        String fullApiResponse = apiService.sendMessage(systemPrompt, userMessage);

        log.info("Raw API Response: {}", fullApiResponse);

        String extractedJson = extractMessage.extractMessage(fullApiResponse);

        log.info("Extracted JSON: {}", extractedJson);

        return extractedJson;
    }

    private String extractTextFromDOCX(MultipartFile file) throws Exception {
        try (
                InputStream inputStream = file.getInputStream();
                XWPFDocument document = new XWPFDocument(inputStream)) {

            StringBuilder text = new StringBuilder();
            List<String> links = new ArrayList<>();

            //extract text
            document.getParagraphs().forEach(paragraph -> text.append(paragraph.getText()).append("\n"));

            //extract links
            for (XWPFHyperlink hyperlink : document.getHyperlinks()) {
                links.add(hyperlink.getURL());
            }

            // Append links to the extracted text
            if (!links.isEmpty()) {
                text.append("\n\nEmbedded Links: \n");
                for (String link : links) {
                    text.append(link).append("\n");
                }
            }

            return text.toString();
        }
    }


    private String extractTextFromPDF(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream();

             PDDocument document = PDDocument.load(inputStream)) {

            //extract text
            PDFTextStripper stripper = new PDFTextStripper();
            StringBuilder text = new StringBuilder(stripper.getText(document));

            //extract link
            List<String> links = extractLinks(document);

            //append these links to text
            if (!links.isEmpty()) {
                text.append("\n\nEmbedded Links: \n");
                for (String link : links) {
                    text.append(link).append("\n");
                }
            }

            return text.toString();
        }
    }

    private List<String> extractLinks(PDDocument document) throws Exception {
        List<String> links = new ArrayList<>();

        // Loop through all the pages and check for annotations (links)
        for (PDPage page : document.getPages()) {
            List<PDAnnotation> annotations = page.getAnnotations();
            for (PDAnnotation annotation : annotations) {
                if (annotation instanceof PDAnnotationLink) {
                    PDAnnotationLink linkAnnotation = (PDAnnotationLink) annotation;
                    // Check if the action is a URI action
                    if (linkAnnotation.getAction() instanceof PDActionURI) {
                        PDActionURI uriAction = (PDActionURI) linkAnnotation.getAction();
                        String uri = uriAction.getURI();
                        links.add(uri);
                    }
                }
            }
        }

        return links;
    }

}





