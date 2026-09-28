package com.feedbackflow.controller;

import com.feedbackflow.dto.ApiResponses;
import com.feedbackflow.dto.LoginRequest;
import com.feedbackflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/login")
    public ApiResponses.Login login(@Valid @RequestBody LoginRequest request) { return service.login(request); }
}