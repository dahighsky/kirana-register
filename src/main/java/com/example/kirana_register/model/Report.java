package com.example.kirana_register.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "reports")
public class Report {
    @Id
    private String id;
    private String userId;
    private ReportType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal netFlow;
    private LocalDateTime generatedAt;

    public enum ReportType {
        WEEKLY, MONTHLY, YEARLY
    }
}