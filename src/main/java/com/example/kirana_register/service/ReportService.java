package com.example.kirana_register.service;

import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.model.Report;
import com.example.kirana_register.repository.TransactionRepository;
import com.example.kirana_register.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReportRepository reportRepository;

    public Map<String, BigDecimal> generateReport(String userId, LocalDateTime start, LocalDateTime end, Report.ReportType type) {
        Optional<Report> cachedReport = reportRepository.findByUserIdAndTypeAndStartDateAndEndDate(userId, type, start, end);

        if (cachedReport.isPresent()) {
            Report report = cachedReport.get();
            return Map.of(
                    "totalCredits", report.getTotalCredits(),
                    "totalDebits", report.getTotalDebits(),
                    "netFlow", report.getNetFlow()
            );
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);

        BigDecimal totalCredits = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.CREDIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netFlow = totalCredits.subtract(totalDebits);

        Report newReport = new Report();
        newReport.setUserId(userId);
        newReport.setType(type);
        newReport.setStartDate(start);
        newReport.setEndDate(end);
        newReport.setTotalCredits(totalCredits);
        newReport.setTotalDebits(totalDebits);
        newReport.setNetFlow(netFlow);
        newReport.setGeneratedAt(LocalDateTime.now());

        reportRepository.save(newReport);

        return Map.of(
                "totalCredits", totalCredits,
                "totalDebits", totalDebits,
                "netFlow", netFlow
        );
    }

    public Map<String, BigDecimal> generateWeeklyReport(String userId, LocalDateTime date) {
        LocalDateTime startOfWeek = date.truncatedTo(ChronoUnit.DAYS).minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        return generateReport(userId, startOfWeek, endOfWeek, Report.ReportType.WEEKLY);
    }

    public Map<String, BigDecimal> generateMonthlyReport(String userId, int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return generateReport(userId, startOfMonth, endOfMonth, Report.ReportType.MONTHLY);
    }

    public Map<String, BigDecimal> generateYearlyReport(String userId, int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = startOfYear.plusYears(1);
        return generateReport(userId, startOfYear, endOfYear, Report.ReportType.YEARLY);
    }
}