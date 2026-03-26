package com.example.AI.interview.controller;


import com.example.AI.interview.model.User;
import com.example.AI.interview.service.Userservice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
   private final Userservice service;


    public UserController(Userservice service) {
        this.service = service;
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return service.saveUser(user);
    }
}
