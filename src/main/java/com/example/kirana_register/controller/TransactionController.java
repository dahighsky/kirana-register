package com.example.kirana_register.controller;

import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction transaction) {
        System.out.println("Inside Post Endpoint");
        return ResponseEntity.ok(transactionService.recordTransaction(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        System.out.println("Inside Get Endpoint cool");
        return ResponseEntity.ok(transactionService.getTransactions(userId, start, end));
    }
}