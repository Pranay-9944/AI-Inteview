package com.example.AI.interview.service;

import com.example.AI.interview.dto.ResumeResponse;
import com.example.AI.interview.exception.ResumeException;
import com.example.AI.interview.model.Resume;
import com.example.AI.interview.repository.ResumeRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    @Value("${resume.upload.dir:uploads/resumes}")
    private String uploadDir;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ResumeResponse uploadResume(String candidateName, String email, MultipartFile file) {
        validateFile(file);

        String savedPath = saveFileToDisk(file);
        String text = "";

        try {
            text = extractTextFromPdf(file);
        } catch (Exception e) {
            // continue with empty text
        }

        Resume resume = new Resume();
        resume.setCandidateName(candidateName);
        resume.setEmail(email);
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setFilePath(savedPath);
        resume.setExtractedText(text);

        // Try AI parsing via Flask, fall back to regex
        try {
            parseWithAI(resume, text);
            resume.setStatus(Resume.ResumeStatus.PARSED);
            resume.setParsedAt(LocalDateTime.now());
        } catch (Exception e) {
            parseWithRegex(resume, text);
            if (resume.getSkills() != null || resume.getJobTitle() != null) {
                resume.setStatus(Resume.ResumeStatus.PARSED);
            } else {
                resume.setStatus(Resume.ResumeStatus.FAILED);
            }
            resume.setParsedAt(LocalDateTime.now());
        }

        return ResumeResponse.from(resumeRepository.save(resume));
    }

    // ── AI parsing via Flask/Groq ────────────────────────────────────────────

    private void parseWithAI(Resume resume, String text) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("resumeText", text.length() > 3000 ? text.substring(0, 3000) : text);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = rt.postForEntity(
                "http://localhost:5000/parse-resume", request, Map.class);

        Map<?, ?> result = response.getBody();
        if (result == null) throw new RuntimeException("Empty AI response");

        if (result.get("skills") instanceof List<?> skillList) {
            resume.setSkills(skillList.stream().map(Object::toString)
                    .collect(Collectors.joining(", ")));
        }
        if (result.get("jobTitle") != null)
            resume.setJobTitle(result.get("jobTitle").toString());
        if (result.get("experienceYears") != null)
            resume.setExperienceYears(result.get("experienceYears").toString());
    }

    // ── Regex fallback ───────────────────────────────────────────────────────

    private void parseWithRegex(Resume resume, String text) {
        resume.setSkills(parseSkills(text));
        resume.setExperienceYears(parseExperienceYears(text));
        resume.setJobTitle(parseJobTitle(text));
    }

    // ── Other service methods ────────────────────────────────────────────────

    public ResumeResponse getById(Long id) {
        return ResumeResponse.from(resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found: " + id)));
    }

    public List<ResumeResponse> getAll() {
        return resumeRepository.findAll().stream()
                .map(ResumeResponse::from).collect(Collectors.toList());
    }

    public String getExtractedText(Long id) {
        Resume r = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found: " + id));
        if (r.getExtractedText() == null || r.getExtractedText().isBlank())
            throw new ResumeException("Resume text not available.");
        return r.getExtractedText();
    }

    public void deleteResume(Long id) {
        Resume r = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found: " + id));
        try { Files.deleteIfExists(Paths.get(r.getFilePath())); } catch (IOException ignored) {}
        resumeRepository.delete(r);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ResumeException("File must not be empty.");
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".pdf"))
            throw new ResumeException("Only PDF files are accepted.");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new ResumeException("File size must not exceed 5 MB.");
    }

    private String saveFileToDisk(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dest = dir.resolve(uniqueName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
            return dest.toString();
        } catch (IOException e) {
            throw new ResumeException("Could not save file: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
    try (InputStream in = file.getInputStream();
         PDDocument doc = Loader.loadPDF(in.readAllBytes())) {
        return new PDFTextStripper().getText(doc);
    }
}
    }

    private String parseSkills(String text) {
        String lower = text.toLowerCase();
        // Try to find skills section
        String[] markers = {"skills", "technical skills", "technologies", "competencies"};
        for (String marker : markers) {
            int idx = lower.indexOf(marker);
            if (idx != -1) {
                int end = Math.min(idx + 500, text.length());
                return text.substring(idx, end)
                        .replaceAll("[\\r\\n]+", ", ").trim();
            }
        }
        return "Not detected";
    }

    private String parseExperienceYears(String text) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(\\d+\\+?)\\s+years?", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(text);
        return m.find() ? m.group(1) + " years" : "Not specified";
    }

    private String parseJobTitle(String text) {
        String[] titles = {
                "software engineer", "software developer", "full stack", "frontend", "backend",
                "data scientist", "data analyst", "machine learning", "devops", "cloud engineer",
                "architect", "analyst", "manager", "lead", "intern", "consultant", "designer"
        };
        for (String line : text.split("\\r?\\n")) {
            String lower = line.toLowerCase().trim();
            for (String kw : titles) {
                if (lower.contains(kw) && line.trim().length() < 80 && line.trim().length() > 3) {
                    return line.trim();
                }
            }
        }
        return "Not detected";
    }
}