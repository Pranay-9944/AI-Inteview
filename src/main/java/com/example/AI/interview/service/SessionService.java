package com.example.AI.interview.service;

import com.example.AI.interview.model.Session;
import com.example.AI.interview.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionService {
    private final SessionRepository repo;

    public SessionService(SessionRepository repo) {
        this.repo = repo;
    }
    public Session startSession(Long userId, String type) {

        Session session = new Session();
        session.setUserId(userId);
        session.setType(type);
        session.setStartTime(LocalDateTime.now());

        return repo.save(session);
    }

    public Session endSession(Long sessionId) {

        Session session = repo.findById(sessionId).orElseThrow();

        session.setEndTime(LocalDateTime.now());

        return repo.save(session);
    }
}
