package com.example.kirana_register.service.impl;

import com.example.kirana_register.dto.request.LoginRequest;
import com.example.kirana_register.dto.request.SignupRequest;
import com.example.kirana_register.dto.response.AuthResponse;
import com.example.kirana_register.model.User;
import com.example.kirana_register.repository.UserRepository;
import com.example.kirana_register.security.CustomUserDetailsService;
import com.example.kirana_register.security.JwtUtil;
import com.example.kirana_register.service.AuthService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           CustomUserDetailsService userDetailsService,
                           JwtUtil jwtUtil,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);

            // Set the JWT token in the response header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwt);

            return ResponseEntity.ok().headers(headers).body(new AuthResponse("Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid username or password"));
        }
    }

    @Override
    public ResponseEntity<AuthResponse> signup(SignupRequest signupRequest) {
        try {
            if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Username already exists"));
            }

            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setRoles(Collections.singleton("ROLE_USER"));
            userRepository.save(user);

            return ResponseEntity.ok(new AuthResponse("User registered successfully"));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse("Username already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("An error occurred during registration"));
        }
    }
}
