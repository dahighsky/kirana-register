package com.example.kirana_register.controller;

import com.example.kirana_register.model.Transaction;
//import com.example.kirana_register.service.DataInitializerService; //to be used only when creating the database for testing
import com.example.kirana_register.service.TransactionService;
import com.example.kirana_register.service.ReportService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;
    private final ReportService reportService;

    // created for initialising database
//    private final DataInitializerService dataInitializerService;

    public TransactionController(TransactionService transactionService, ReportService reportService) {
        this.transactionService = transactionService;
        this.reportService = reportService;
//        this.dataInitializerService = dataInitializerService;
    }

    @PostMapping("/transactions")
    @CircuitBreaker(name = "recordTransaction", fallbackMethod = "recordTransactionFallback")
    @RateLimiter(name = "recordTransaction")
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.recordTransaction(transaction));
    }

    public ResponseEntity<Transaction> recordTransactionFallback(Transaction transaction, Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @GetMapping("/transactions")
    @CircuitBreaker(name = "getTransactions", fallbackMethod = "getTransactionsFallback")
    @RateLimiter(name = "getTransactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactions(userId, start, end));
    }

    public ResponseEntity<List<Transaction>> getTransactionsFallback(String userId, LocalDateTime start, LocalDateTime end, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @GetMapping("/reports/weekly")
    @CircuitBreaker(name = "weeklyReport", fallbackMethod = "getWeeklyReportFallback")
    @RateLimiter(name = "weeklyReport")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getWeeklyReport(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateWeeklyReport(userId, date);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getWeeklyReportFallback(String userId, LocalDateTime date, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @GetMapping("/reports/monthly")
    @CircuitBreaker(name = "monthlyReport", fallbackMethod = "getMonthlyReportFallback")
    @RateLimiter(name = "monthlyReport")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getMonthlyReport(
            @RequestParam String userId,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateMonthlyReport(userId, year, month);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getMonthlyReportFallback(String userId, int year, int month, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @GetMapping("/reports/yearly")
    @CircuitBreaker(name = "yearlyReport", fallbackMethod = "getYearlyReportFallback")
    @RateLimiter(name = "yearlyReport")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getYearlyReport(
            @RequestParam String userId,
            @RequestParam int year) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateYearlyReport(userId, year);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getYearlyReportFallback(String userId, int year, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

//    @GetMapping("/initialize") // for initialising database;
//    public String initializeData() {
//        return dataInitializerService.initializeData();
//    }

//    @GetMapping("/convertCurrencies")
//    public String convertAmounts()  {
//        return dataInitializerService.updateAllTransactionsWithConvertedAmounts();
//    }
}