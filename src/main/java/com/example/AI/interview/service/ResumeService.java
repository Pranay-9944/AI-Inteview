package com.example.AI.interview.service;

import com.example.AI.interview.dto.ResumeResponse;
import com.example.AI.interview.exception.ResumeException;
import com.example.AI.interview.model.Resume;
import com.example.AI.interview.repository.ResumeRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    @Value("${resume.upload.dir:uploads/resumes}")
    private String uploadDir;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ResumeResponse uploadResume(String candidateName,
                                       String email,
                                       MultipartFile file) {
        validateFile(file);

        String savedPath = saveFileToDisk(file);

        Resume resume = new Resume();
        resume.setCandidateName(candidateName);
        resume.setEmail(email);
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setFilePath(savedPath);

        try {
            String text = extractTextFromPdf(file);
            resume.setExtractedText(text);
            resume.setSkills(parseSkills(text));
            resume.setExperienceYears(parseExperienceYears(text));
            resume.setJobTitle(parseJobTitle(text));
            resume.setStatus(Resume.ResumeStatus.PARSED);
            resume.setParsedAt(LocalDateTime.now());
        } catch (Exception e) {
            resume.setStatus(Resume.ResumeStatus.FAILED);
        }

        Resume saved = resumeRepository.save(resume);
        return ResumeResponse.from(saved);
    }

    public ResumeResponse getById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found with id: " + id));
        return ResumeResponse.from(resume);
    }

    public List<ResumeResponse> getAll() {
        return resumeRepository.findAll()
                .stream()
                .map(ResumeResponse::from)
                .collect(Collectors.toList());
    }

    public String getExtractedText(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found with id: " + id));
        if (resume.getExtractedText() == null || resume.getExtractedText().isBlank()) {
            throw new ResumeException("Resume text not available. Parsing may have failed.");
        }
        return resume.getExtractedText();
    }

    public void deleteResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeException("Resume not found with id: " + id));
        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException ignored) { }
        resumeRepository.delete(resume);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResumeException("File must not be empty.");
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".pdf")) {
            throw new ResumeException("Only PDF files are accepted.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResumeException("File size must not exceed 5 MB.");
        }
    }

    private String saveFileToDisk(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = dir.resolve(uniqueFileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return destination.toString();
        } catch (IOException e) {
            throw new ResumeException("Could not save file: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            PDDocument doc = Loader.loadPDF(in.readAllBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            doc.close();
            return text;
        }
    }

    private String parseSkills(String text) {
        String lower = text.toLowerCase();
        int idx = lower.indexOf("skills");
        if (idx == -1) return null;
        int end = Math.min(idx + 300, text.length());
        return text.substring(idx, end).replaceAll("[\\r\\n]+", ", ").trim();
    }

    private String parseExperienceYears(String text) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(\\d+\\+?)\\s+years?", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(text);
        return m.find() ? m.group(1) + " years" : null;
    }

    private String parseJobTitle(String text) {
        String[] titleKeywords = {
                "software engineer", "developer", "architect", "analyst",
                "manager", "lead", "intern", "consultant", "designer"
        };
        for (String line : text.split("\\r?\\n")) {
            String lower = line.toLowerCase().trim();
            for (String kw : titleKeywords) {
                if (lower.contains(kw) && line.length() < 80) {
                    return line.trim();
                }
            }
        }
        return null;
    }
}