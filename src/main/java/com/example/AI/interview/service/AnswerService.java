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

        Map<String, String> requestBody = Map.of("answer", answer.getAnswer());
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        // ❌ REMOVED: HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
        // That was a duplicate declaration referencing a non-existent `requestJson` variable

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

    // `escape()` is now unused — safe to delete since Map serialization handles escaping
}