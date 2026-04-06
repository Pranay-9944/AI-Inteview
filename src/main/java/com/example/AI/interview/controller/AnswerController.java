package com.example.AI.interview.controller;

import com.example.AI.interview.model.Answer;
import com.example.AI.interview.service.AnswerService;
import com.example.AI.interview.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnswerController {

    private final AnswerService answerService;
    private final ResumeService resumeService;

    public AnswerController(AnswerService answerService, ResumeService resumeService) {
        this.answerService = answerService;
        this.resumeService = resumeService;
    }

    /** POST /api/answers — submit and evaluate an answer */
    @PostMapping("/answers")
    public ResponseEntity<Answer> submit(@RequestBody Answer answer) {
        return ResponseEntity.ok(answerService.submitAnswer(answer));
    }

    /**
     * POST /api/questions/generate
     * Body: { "resumeId": 1, "type": "AI", "count": 5 }
     * Proxies to Python AI service, returns { "questions": [...] }
     */
    @PostMapping("/questions/generate")
    public ResponseEntity<?> generateQuestions(@RequestBody Map<String, Object> body) {
        Long resumeId = Long.valueOf(body.get("resumeId").toString());
        String type   = body.getOrDefault("type", "AI").toString();
        int count     = Integer.parseInt(body.getOrDefault("count", 5).toString());

        String resumeText;
        try {
            resumeText = resumeService.getExtractedText(resumeId);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Resume not found or text not extracted: " + e.getMessage()));
        }

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> aiRequest = Map.of(
                "resumeText", resumeText,
                "type",       type,
                "count",      count
        );

        try {
            ResponseEntity<Map> resp = rt.postForEntity(
                    "http://localhost:5000/generate-questions",
                    new HttpEntity<>(aiRequest, headers),
                    Map.class
            );
            return ResponseEntity.ok(resp.getBody());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "AI service error: " + e.getMessage()));
        }
    }
}