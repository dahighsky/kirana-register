package com.example.kirana_register.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Generate the secure HS256 key
        byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String base64EncodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Base64 Encoded JWT Key: " + base64EncodedKey);
    }
}
