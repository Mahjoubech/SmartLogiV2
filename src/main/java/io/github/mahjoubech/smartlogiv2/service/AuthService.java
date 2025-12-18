package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.LoginRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout();
}
