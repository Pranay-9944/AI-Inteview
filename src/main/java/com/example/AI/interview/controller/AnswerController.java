package com.example.AI.interview.controller;


import com.example.AI.interview.model.Answer;
import com.example.AI.interview.service.AnswerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService service;

    public AnswerController(AnswerService service) {
        this.service = service;
    }

    @PostMapping("/submit")
    public Answer submit(@RequestBody Answer answer) {
        return service.submitAnswer(answer);
    }
}