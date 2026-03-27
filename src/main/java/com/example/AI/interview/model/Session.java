package com.example.AI.interview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private String type; // AI / HR / DSA

    private LocalDateTime startTime;

    private LocalDateTime endTime;


    public Long getId()
    {
        return id;

    }
    public void setUserId(Long id)
    {
        this.id= id;
    }
    public String gettype()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type=type;

    }

    public LocalDateTime getstartTime(){
        return  startTime;
    }
    public void setStartTime(  LocalDateTime startTime)
    {
        this.startTime=startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public  void setEndTime(LocalDateTime endTime)
    {
        this.endTime=endTime;
    }
}
