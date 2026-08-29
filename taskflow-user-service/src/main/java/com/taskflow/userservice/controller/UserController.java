package com.taskflow.userservice.controller;

import com.taskflow.userservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository; 
    }

    @GetMapping("/{username}/exists")
    public boolean userExists(@PathVariable String username) {
        return userRepository.existsByUsername(username);

    }

}
