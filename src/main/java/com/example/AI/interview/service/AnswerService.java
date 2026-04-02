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

        String requestJson = "{\"answer\":\"" + answer.getAnswer() + "\"}";

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map body = response.getBody();

        int score = (int) body.get("score");
        String feedback = (String) body.get("feedback");

        answer.setScore(score);
        answer.setFeedback(feedback);

        return repo.save(answer);
    }
}