package com.example.kirana_register.service;

import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction recordTransaction(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(String userId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);
    }
}