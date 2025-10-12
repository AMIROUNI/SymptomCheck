package com.SymptomCheck.userservice.controllers;

import com.SymptomCheck.userservice.repositories.UserRepository;
import com.SymptomCheck.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userServicemv;



    @GetMapping()
    public ResponseEntity<String> getUsers() {
        return   ResponseEntity.ok("hello");
    }

}
