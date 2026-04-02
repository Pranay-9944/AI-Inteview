package com.example.AI.interview.controller;

import com.example.AI.interview.dto.ResumeResponse;
import com.example.AI.interview.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> upload(
            @RequestParam("file")          MultipartFile file,
            @RequestParam("candidateName") String candidateName,
            @RequestParam("email")         String email) {

        ResumeResponse response = resumeService.uploadResume(candidateName, email, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAll() {
        return ResponseEntity.ok(resumeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully."));
    }
}