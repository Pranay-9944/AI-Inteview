package com.example.AI.interview.repository;

import com.example.AI.interview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
