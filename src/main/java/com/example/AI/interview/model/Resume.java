package com.example.AI.interview.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    private String skills;
    private String experienceYears;
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResumeStatus status = ResumeStatus.UPLOADED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    private LocalDateTime parsedAt;

    public Long getId() { return id; }

    public String getCandidateName() {
        return candidateName; }

    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getEmail()
    { return email;
    }
    public void setEmail(String email) { this.email = email; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperienceYears() { return experienceYears; }
    public void setExperienceYears(String experienceYears) { this.experienceYears = experienceYears; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public ResumeStatus getStatus() { return status; }
    public void setStatus(ResumeStatus status) { this.status = status; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public LocalDateTime getParsedAt() { return parsedAt; }
    public void setParsedAt(LocalDateTime parsedAt) { this.parsedAt = parsedAt; }

    public enum ResumeStatus {
        UPLOADED,
        PARSED,
        FAILED
    }
}

