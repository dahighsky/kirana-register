package com.example.kirana_register.service.impl;

import com.example.kirana_register.dto.response.ReportResponse;
import com.example.kirana_register.model.Report;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.ReportRepository;
import com.example.kirana_register.repository.TransactionRepository;
import com.example.kirana_register.service.ReportService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final ReportRepository reportRepository;
    private static final List<String> SUPPORTED_CURRENCIES = List.of("INR", "USD", "EUR");

    public ReportServiceImpl(TransactionRepository transactionRepository, ReportRepository reportRepository) {
        this.transactionRepository = transactionRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public ReportResponse generateReport(String userId, LocalDateTime start, LocalDateTime end, Report.ReportType type) {
        Optional<Report> cachedReport = reportRepository.findByUserIdAndTypeAndStartDateAndEndDate(userId, type, start, end);

        if (cachedReport.isPresent()) {
            Report report = cachedReport.get();
            return new ReportResponse(report);
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);

        Map<String, BigDecimal> totalCredits = calculateTotalsByCurrency(transactions, Transaction.TransactionType.CREDIT);
        Map<String, BigDecimal> totalDebits = calculateTotalsByCurrency(transactions, Transaction.TransactionType.DEBIT);
        Map<String, BigDecimal> netFlow = calculateNetFlow(totalCredits, totalDebits);

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

        return new ReportResponse(newReport);
    }

    @Override
    public ReportResponse generateWeeklyReport(String userId, LocalDateTime date) {
        LocalDateTime startOfWeek = date.truncatedTo(ChronoUnit.DAYS).minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        if (endOfWeek.isAfter(LocalDateTime.now()))
            endOfWeek = LocalDateTime.now();
        return generateReport(userId, startOfWeek, endOfWeek, Report.ReportType.WEEKLY);
    }

    @Override
    public ReportResponse generateMonthlyReport(String userId, Year year, Month month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year.getValue(), month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        if (endOfMonth.isAfter(LocalDateTime.now()))
            endOfMonth = LocalDateTime.now();
        return generateReport(userId, startOfMonth, endOfMonth, Report.ReportType.MONTHLY);
    }

    @Override
    public ReportResponse generateYearlyReport(String userId, @NotNull Year year) {
        LocalDateTime startOfYear = LocalDateTime.of(year.getValue(), 1, 1, 0, 0);
        LocalDateTime endOfYear = startOfYear.plusYears(1);
        if (endOfYear.isAfter(LocalDateTime.now()))
            endOfYear = LocalDateTime.now();
        return generateReport(userId, startOfYear, endOfYear, Report.ReportType.YEARLY);
    }

    private Map<String, BigDecimal> calculateTotalsByCurrency(List<Transaction> transactions, Transaction.TransactionType type) {
        return SUPPORTED_CURRENCIES.stream()
                .collect(Collectors.toMap(
                        currency -> currency,
                        currency -> transactions.stream()
                                .filter(t -> t.getType() == type)
                                .map(t -> t.getConvertedAmounts().getOrDefault(currency, BigDecimal.ZERO))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ));
    }

    private Map<String, BigDecimal> calculateNetFlow(Map<String, BigDecimal> totalCredits, Map<String, BigDecimal> totalDebits) {
        return SUPPORTED_CURRENCIES.stream()
                .collect(Collectors.toMap(
                        currency -> currency,
                        currency -> totalCredits.getOrDefault(currency, BigDecimal.ZERO)
                                .subtract(totalDebits.getOrDefault(currency, BigDecimal.ZERO))
                ));
    }
}