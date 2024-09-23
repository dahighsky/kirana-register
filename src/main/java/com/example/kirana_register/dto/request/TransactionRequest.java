package com.example.kirana_register.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class TransactionRequest {
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String type;
    private Boolean isRefund = false;
}
