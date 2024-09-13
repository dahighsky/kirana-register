package com.example.kirana_register.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private LocalDateTime timestamp;

    public enum TransactionType {
        CREDIT, DEBIT
    }
}