package com.example.kirana_register.service;

import com.example.kirana_register.model.Transaction;

import java.time.LocalDateTime;

public interface RefundService {
    public Transaction refundTransaction(String userId, LocalDateTime start, LocalDateTime end);
}
