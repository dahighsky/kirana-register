package com.example.kirana_register.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuthResponse {
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }
}
