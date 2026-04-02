package com.example.AI.interview.dto;

import com.example.AI.interview.model.Resume;
import java.time.LocalDateTime;

public class ResumeResponse {

    private Long id;
    private String candidateName;
    private String email;
    private String originalFileName;
    private String skills;
    private String experienceYears;
    private String jobTitle;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime parsedAt;

    public static ResumeResponse from(Resume r) {
        ResumeResponse dto = new ResumeResponse();
        dto.id               = r.getId();
        dto.candidateName    = r.getCandidateName();
        dto.email            = r.getEmail();
        dto.originalFileName = r.getOriginalFileName();
        dto.skills           = r.getSkills();
        dto.experienceYears  = r.getExperienceYears();
        dto.jobTitle         = r.getJobTitle();
        dto.status           = r.getStatus().name();
        dto.uploadedAt       = r.getUploadedAt();
        dto.parsedAt         = r.getParsedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getCandidateName() { return candidateName; }
    public String getEmail() { return email; }
    public String getOriginalFileName() { return originalFileName; }
    public String getSkills() { return skills; }
    public String getExperienceYears() { return experienceYears; }
    public String getJobTitle() { return jobTitle; }
    public String getStatus() { return status; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public LocalDateTime getParsedAt() { return parsedAt; }
}