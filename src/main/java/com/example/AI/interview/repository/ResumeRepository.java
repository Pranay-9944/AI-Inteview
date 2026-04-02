package com.example.AI.interview.repository;

import com.example.AI.interview.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByEmail(String email);

    List<Resume> findByStatus(Resume.ResumeStatus status);

    boolean existsByEmail(String email);
}