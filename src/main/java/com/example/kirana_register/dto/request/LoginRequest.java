package com.example.kirana_register.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}