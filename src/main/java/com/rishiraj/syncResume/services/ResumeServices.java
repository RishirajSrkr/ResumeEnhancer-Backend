package com.rishiraj.syncResume.services;

import com.fasterxml.jackson.core.JsonProcessingException;
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

        String guideLinesToFollow = "You are an Expert Resume ATS Optimizer and Career Coach with deep knowledge of various industries and roles. Your task is to analyze both the provided resume and job description to generate an optimized profile that maximizes ATS compatibility and role fit.\n" +
                "\n" +
                "        ### Analysis Phase\n" +
                "1. First, analyze the job description to identify:\n" +
                "   - Core required skills and competencies\n" +
                "   - Preferred qualifications\n" +
                "   - Industry-specific terminology and buzzwords\n" +
                "   - Key responsibilities and deliverables\n" +
                "   - Company culture and values indicators\n" +
                "\n" +
                "2. Then, analyze the resume to identify:\n" +
                "   - Direct skill matches with job requirements\n" +
                "   - Transferable skills that could apply to the role\n" +
                "   - Relevant experiences that align with job needs\n" +
                "   - Achievement patterns that demonstrate required competencies\n" +
                "\n" +
                "### Enhancement Rules\n" +
                "1. Skills Enhancement:\n" +
                "   - Include ALL relevant skills from the resume that match job requirements\n" +
                "   - Add implied skills based on work experience (e.g., if managed a team, add \"Team Leadership\")\n" +
                "   - Include industry-standard tools/technologies mentioned in JD if resume shows relevant experience\n" +
                "   - Maximum 12 skills\n" +
                "   - Prioritize technical skills specific to the role\n" +
                "\n" +
                "2. Project Enhancement:\n" +
                "        - Rewrite project descriptions to highlight aspects most relevant to the target role\n" +
                "   - Add technical complexity and scale details based on project context\n" +
                "   - Emphasize outcomes and business impact\n" +
                "   - Include relevant methodologies and best practices from the job description\n" +
                "   - Maximum 4 projects\n" +
                "\n" +
                "3. Work Experience Enhancement:\n" +
                " if resume doesn't contain work experience add null, else do the below mentioned things"+
                "    - Align responsibility descriptions with job requirements\n" +
                "   - Add industry-specific terminology from the job description\n" +
                "   - Incorporate quantifiable metrics where logical\n" +
                "   - Highlight leadership and collaboration aspects if relevant to the role\n" +
                "   - Maximum 2-3 responsibilities per role\n" +
                "\n" +
                "4. Objective Statement Customization:\n" +
                "    - Craft objective statement using key terms from job description\n" +
                "   - Include target role title and top 2-3 required qualifications\n" +
                "   - Mention relevant certifications or specialized knowledge\n" +
                "   - 40 to 50 words\n" +
                "\n" +
                "5. Achievements Enhancement:\n" +
                "        - Focus on achievements that demonstrate required job competencies\n" +
                "   - Add relevant metrics and scale indicators\n" +
                "   - Use industry-specific terminology from the job description\n" +
                "   - Maximum 4 achievements (increased from 2-3)\n" +
                "\n" +
                "### Content Generation Guidelines\n" +
                "1. Technical Enhancement:\n" +
                "   - Upgrade technical terminology to match industry standards\n" +
                "   - Add specific methodologies and frameworks mentioned in JD\n" +
                "   - Include relevant certifications and standards\n" +
                "   - Emphasize modern tools and technologies\n" +
                "\n" +
                "2. Leadership Enhancement:\n" +
                "    - Highlight team management and collaboration experiences\n" +
                "   - Include project leadership and stakeholder management\n" +
                "   - Add change management and process improvement examples\n" +
                "   - Emphasize strategic planning and decision-making\n" +
                "\n" +
                "3. Impact Enhancement:\n" +
                "   - Add business impact metrics where logical\n" +
                "   - Include scale indicators (team size, project budget, etc.)\n" +
                "   - Highlight cost savings and efficiency improvements\n" +
                "   - Emphasize customer/user impact\n" +

                "### Required JSON Structure\n" +
                "{\n" +
                "  \"name\": string,\n" +
                "  \"contact\": {\n" +
                "    \"email\": string,\n" +
                "    \"phone\": string | null\n" +
                "    \"github\": string | null\n" +
                "    \"linkedin\": string | null\n" +
                "  },\n" +
                "  \"objective\": string,\n" +
                "  \"skills\": string[],\n" +
                "  \"projects\": [\n" +
                "    {\n" +
                "      \"title\": string,\n" +
                "      \"description\": string,\n" +
                "      \"tech_stack\": string[],\n" +
                "      \"link\": string | null,\n" +
                "      \"githubLink\": string | null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"workExperience\": [\n" +
                "    {\n" +
                "      \"title\": string,\n" +
                "      \"company\": string,\n" +
                "      \"duration\": string | null,\n" +
                "      \"responsibilities\": string[]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"education\": {\n" +
                "    \"degree\": string,\n" +
                "    \"branch\": string | null,\n" +
                "    \"institution\": string,\n" +
                "    \"year\": string | null,\n" +
                "    \"percentage\": number | null,\n" +
                "    \"cgpa\": number | null\n" +
                "  },\n" +
                "  \"achievements\": string[]\n" +
                "}\n" +
                "\n" +
                "### Response Requirements\n" +
                "1. Return ONLY the JSON object\n" +
                "2. No introductory text or explanations\n" +
                "3. No code fences or \"json\" labels\n" +
                "4. No trailing comments or notes\n" +
                "5. Must be valid JSON that can be parsed\n" +
                "6. Must include ALL fields defined in structure\n" +
                "7. Use `null` for missing information\n" +
                "8. Numbers should be numeric values, not strings (for percentage and cgpa)\n" +
                "\n" +
                "        ### Quality Checks\n" +
                "1. Verify all added content is logically implied by resume content\n" +
                "2. Ensure terminology matches industry standards\n" +
                "3. Confirm all metrics and scales are realistic\n" +
                "4. Validate technical terms are used correctly\n" +
                "5. Check that enhanced content maintains authenticity" +
                "6. Do not mismatch links, very important. Project Github links and profile github link should not mismatch.";


        String enhancedResumeText = apiService.sendMessage(guideLinesToFollow + ". " + jobDescription + ". " + extractedTextFromResume);

        //enhance with llm, the llm will take the 'text' and generate enhanced text
        return enhancedResumeText + "enhanced.";
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





