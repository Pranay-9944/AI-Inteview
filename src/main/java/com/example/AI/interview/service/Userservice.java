package com.example.AI.interview.service;


import com.example.AI.interview.model.User;
import com.example.AI.interview.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class Userservice
{

    private final UserRepository repo;

    public Userservice(UserRepository repo) {
        this.repo = repo;
    }
    public User saveUser(User user)
    {
        return  repo.save(user);
    }




}

