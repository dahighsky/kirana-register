package com.example.kirana_register.service;


import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.TransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionResponse recordTransaction(TransactionRequest transactionRequestDTO);

    List<TransactionResponse> getTransactions(String userId, LocalDateTime start, LocalDateTime end);
}
