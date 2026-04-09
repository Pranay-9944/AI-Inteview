package com.example.AI.interview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String type;           // AI | HR | DSA
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getId()                     { return id; }
    public Long getUserId()                 { return userId; }
// ✅ Should be
public void setUserId(Long userId) {
    this.userId = userId;
}    public String getType()                 { return type; }
    public void setType(String type)        { this.type = type; }
    public LocalDateTime getStartTime()     { return startTime; }
    public void setStartTime(LocalDateTime t){ this.startTime = t; }
    public LocalDateTime getEndTime()       { return endTime; }
    public void setEndTime(LocalDateTime t) { this.endTime = t; }
}