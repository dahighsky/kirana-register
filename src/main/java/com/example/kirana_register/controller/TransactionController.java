package com.example.kirana_register.controller;

import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.ReportResponse;
import com.example.kirana_register.dto.response.TransactionResponse;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.service.TransactionService;
import com.example.kirana_register.service.ReportService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@Validated
@Tag(name = "Transaction", description = "Transaction management APIs")
@SecurityRequirement(name = "Authorization")
public class TransactionController {

    private final TransactionService transactionService;
    private final ReportService reportService;

    public TransactionController(TransactionService transactionService, ReportService reportService) {
        this.transactionService = transactionService;
        this.reportService = reportService;
    }

    @PostMapping("/")
    @CircuitBreaker(name = "recordTransaction", fallbackMethod = "recordTransactionFallback")
    @RateLimiter(name = "recordTransaction")
    @Operation(summary = "Record a new transaction", description = "Creates a new transaction record")
    @ApiResponse(responseCode = "200", description = "Transaction recorded successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "503", description = "Service unavailable")
    public ResponseEntity<TransactionResponse> recordTransaction(@Valid @NotNull @RequestBody TransactionRequest transaction) {
        return ResponseEntity.ok(transactionService.recordTransaction(transaction));
    }

    public ResponseEntity<Transaction> recordTransactionFallback(TransactionRequest transaction, Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @GetMapping("/")
    @CircuitBreaker(name = "getTransactions", fallbackMethod = "getTransactionsFallback")
    @RateLimiter(name = "getTransactions")
    @Operation(summary = "Get transactions", description = "Retrieves transactions for a user within a date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "429", description = "Too many requests")
    @ApiResponse(responseCode = "503", description = "Service unavailable")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @Parameter(description = "User ID", required = true) @NotNull @RequestParam String userId,
            @Parameter(description = "Start date and time", required = true) @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date and time", required = true) @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactions(userId, start, end));
    }

    public ResponseEntity<List<Transaction>> getTransactionsFallback(String userId, LocalDateTime start, LocalDateTime end, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @GetMapping("/reports/weekly")
    @CircuitBreaker(name = "weeklyReport", fallbackMethod = "getWeeklyReportFallback")
    @RateLimiter(name = "weeklyReport")
    @Operation(summary = "Get weekly report", description = "Generates a weekly report for a user")
    @ApiResponse(responseCode = "200", description = "Successfully generated weekly report")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "429", description = "Too many requests")
    @ApiResponse(responseCode = "503", description = "Service unavailable")
    public ResponseEntity<ReportResponse> getWeeklyReport(
            @Parameter(description = "User ID", required = true) @NotNull @RequestParam String userId,
            @Parameter(description = "Report date", required = true) @NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        ReportResponse report = reportService.generateWeeklyReport(userId, date);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<ReportResponse> getWeeklyReportFallback(String userId, LocalDateTime date, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @GetMapping("/reports/monthly")
    @CircuitBreaker(name = "monthlyReport", fallbackMethod = "getMonthlyReportFallback")
    @RateLimiter(name = "monthlyReport")
    @Operation(summary = "Get monthly report", description = "Generates a monthly report for a user")
    @ApiResponse(responseCode = "200", description = "Successfully generated monthly report")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "429", description = "Too many requests")
    @ApiResponse(responseCode = "503", description = "Service unavailable")
    public ResponseEntity<ReportResponse> getMonthlyReport(
            @Parameter(description = "User ID", required = true) @NotNull @RequestParam String userId,
            @Parameter(description = "Year", required = true) @NotNull @RequestParam Year year,
            @Parameter(description = "Month", required = true) @NotNull @RequestParam Month month) {
        ReportResponse report = reportService.generateMonthlyReport(userId, year, month);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<ReportResponse> getMonthlyReportFallback(String userId, Year year, Month month, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @GetMapping("/reports/yearly")
    @CircuitBreaker(name = "yearlyReport", fallbackMethod = "getYearlyReportFallback")
    @RateLimiter(name = "yearlyReport")
    @Operation(summary = "Get yearly report", description = "Generates a yearly report for a user")
    @ApiResponse(responseCode = "200", description = "Successfully generated yearly report")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "429", description = "Too many requests")
    @ApiResponse(responseCode = "503", description = "Service unavailable")
    public ResponseEntity<ReportResponse> getYearlyReport(
            @Parameter(description = "User ID", required = true) @NotNull @RequestParam String userId,
            @Parameter(description = "Year", required = true) @NotNull @RequestParam Year year) {
        ReportResponse report = reportService.generateYearlyReport(userId, year);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<ReportResponse> getYearlyReportFallback(String userId, Year year, Exception e) {
        if (e instanceof RequestNotPermitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}