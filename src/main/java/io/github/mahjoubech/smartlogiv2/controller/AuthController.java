package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.AuthResponse;
import io.github.mahjoubech.smartlogiv2.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
         AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
    @PostMapping("/login")
    public String login() {
        return "Hello from AuthController";
    }
    @PostMapping("/refrech")
    public String refreshToken() {
        return "Hello from AuthController";
    }
    @PostMapping("/logout")
    public String logout() {
        return "Hello from AuthController";
    }
}
