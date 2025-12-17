package io.github.mahjoubech.smartlogiv2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/register")
    public String register() {
        return "Hello from AuthController";
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
