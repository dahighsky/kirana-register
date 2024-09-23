package com.example.kirana_register.service.impl;

import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.TransactionResponse;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import com.example.kirana_register.service.TransactionService;

import java.time.LocalDateTime;

public class RefundServiceImpl {

    private final TransactionRepository transactionRepository;
    private  final TransactionService transactionService;

    public ReportServiceImpl(TransactionRepository transactionRepository, TransactionService transactionService){
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService
    }

    public TransactionResponse refundTransaction(String transactionId){
        Transaction transaction = transactionRepository.findById(transactionId);

        TransactionResponse newTransaction;

        if(transaction != null) {
            transaction.setRefunded(true);

            TransactionRequest newTransactionRequest = new TransactionRequest(transaction.getUserId(), transaction.getAmount(), transaction.getCurrency(), "DEBIT");
            newTransaction = transactionService.recordTransaction(newTransactionRequest);
        }

        if(newTransaction != null){
            return  newTransaction;
        }
    };
}
