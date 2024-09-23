package com.example.kirana_register.service.impl;

import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.TransactionResponse;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import com.example.kirana_register.service.RefundService;
import com.example.kirana_register.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class RefundServiceImpl implements RefundService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public RefundServiceImpl(TransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public TransactionResponse refundTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (transaction.getRefunded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction has already been refunded");
        }

        transaction.setRefunded(true);
        transactionRepository.save(transaction);

        TransactionRequest newTransactionRequest = new TransactionRequest(
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                "DEBIT",
                true
        );

        return transactionService.recordTransaction(newTransactionRequest);
    }
}