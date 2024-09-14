package com.example.kirana_register.controller;

import com.example.kirana_register.model.Transaction;
//import com.example.kirana_register.service.DataInitializerService; //to be used only when creating the database for testing
import com.example.kirana_register.service.TransactionService;
import com.example.kirana_register.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    public TransactionController(TransactionService transactionService, ReportService reportService){
        this.transactionService = transactionService;
        this.reportService = reportService;
//        this.dataInitializerService = dataInitializerService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.recordTransaction(transaction));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactions(userId, start, end));
    }

    @GetMapping("/reports/weekly")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getWeeklyReport(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateWeeklyReport(userId, date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/monthly")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getMonthlyReport(
            @RequestParam String userId,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateMonthlyReport(userId, year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/yearly")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getYearlyReport(
            @RequestParam String userId,
            @RequestParam int year) {
        Map<String, Map<String, BigDecimal>> report = reportService.generateYearlyReport(userId, year);
        return ResponseEntity.ok(report);
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