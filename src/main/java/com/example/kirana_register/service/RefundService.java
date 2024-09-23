package com.example.kirana_register.service;

import com.example.kirana_register.dto.response.TransactionResponse;


public interface RefundService {
    public TransactionResponse refundTransaction(String transactionId);
}
