package com.example.kirana_register.service;

import com.example.kirana_register.dto.request.LoginRequest;
import com.example.kirana_register.dto.request.SignupRequest;
import com.example.kirana_register.dto.response.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthResponse> login(LoginRequest loginRequest);
    ResponseEntity<AuthResponse> signup(SignupRequest signupRequest);
}
