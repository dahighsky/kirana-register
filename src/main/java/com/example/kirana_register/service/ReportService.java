package com.example.kirana_register.service;

import com.example.kirana_register.dto.response.ReportResponse;
import com.example.kirana_register.model.Report;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

public interface ReportService {
    ReportResponse generateReport(String userId, LocalDateTime start, LocalDateTime end, Report.ReportType type);
    ReportResponse generateWeeklyReport(String userId, LocalDateTime date);
    ReportResponse generateMonthlyReport(String userId, Year year, Month month);
    ReportResponse generateYearlyReport(String userId, @NotNull Year year);
}