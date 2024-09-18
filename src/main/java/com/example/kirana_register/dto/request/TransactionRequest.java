package com.example.kirana_register.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String description;
}
