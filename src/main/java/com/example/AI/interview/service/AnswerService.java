package com.example.AI.interview.service;

import com.example.AI.interview.model.Answer;
import com.example.AI.interview.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Service
public class AnswerService {

    private final AnswerRepository repo;

    public AnswerService(AnswerRepository repo) {
        this.repo = repo;
    }

    public Answer submitAnswer(Answer answer) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:5000/evaluate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send question + answer + type so Groq has full context
        String requestJson = String.format(
                "{\"answer\":\"%s\",\"question\":\"%s\",\"type\":\"%s\"}",
                escape(answer.getAnswer()),
                escape(answer.getType()),   // re-using type field for question text is optional;
                answer.getType() != null ? answer.getType() : "AI"
        );

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map body = response.getBody();
            if (body != null) {
                Object scoreObj = body.get("score");
                answer.setScore(scoreObj instanceof Integer ? (Integer) scoreObj
                        : Integer.parseInt(scoreObj.toString()));
                answer.setFeedback((String) body.get("feedback"));
            }
        } catch (Exception e) {
            answer.setScore(0);
            answer.setFeedback("AI service unavailable: " + e.getMessage());
        }

        return repo.save(answer);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
    }
}