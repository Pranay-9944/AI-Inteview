package com.example.AI.interview.repository;

import com.example.AI.interview.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository  extends JpaRepository<Session,Long> {


}
