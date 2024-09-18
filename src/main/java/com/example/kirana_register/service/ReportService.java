package com.example.kirana_register.service;

import com.example.kirana_register.dto.response.ReportResponse;
import com.example.kirana_register.model.Report;

import java.time.LocalDateTime;

public interface ReportService {
    ReportResponse generateReport(String userId, LocalDateTime start, LocalDateTime end, Report.ReportType type);
    ReportResponse generateWeeklyReport(String userId, LocalDateTime date);
    ReportResponse generateMonthlyReport(String userId, int year, int month);
    ReportResponse generateYearlyReport(String userId, int year);
}