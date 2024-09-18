package com.example.kirana_register.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TransactionResponse {
    private String transactionId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private Map<String, BigDecimal> convertedAmounts;
}
