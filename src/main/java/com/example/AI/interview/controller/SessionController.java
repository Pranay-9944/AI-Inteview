package com.example.AI.interview.controller;


import com.example.AI.interview.model.Session;
import com.example.AI.interview.service.SessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public Session start(@RequestParam Long userId,
                         @RequestParam String type) {
        return service.startSession(userId, type);
    }

    @PostMapping("/end")
    public Session end(@RequestParam Long sessionId) {
        return service.endSession(sessionId);
    }
}
