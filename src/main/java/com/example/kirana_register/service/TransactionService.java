package com.example.kirana_register.service;

import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WebClient webClient;

    private static final String FX_RATES_API_URL = "https://api.fxratesapi.com/latest";
    private static final List<String> TARGET_CURRENCIES = List.of("INR", "USD", "EUR");

    public TransactionService(TransactionRepository transactionRepository, WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.webClient = webClientBuilder.baseUrl(FX_RATES_API_URL).build();
    }

    @Cacheable(value = "exchangeRates", key = "'latestRates'")
    public Map<String, BigDecimal> getExchangeRates() {
        return webClient.get()
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(ExchangeRateResponse::rates)
                .block();
    }

    @Scheduled(fixedRate = 900000) // 15 minutes
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void clearExchangeRatesCache() {
        // This method will clear the cache every 15 minutes
    }

    public Transaction recordTransaction(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());

        Map<String, BigDecimal> exchangeRates = getExchangeRates();
        Map<String, BigDecimal> convertedAmounts = new HashMap<>();

        BigDecimal amount = transaction.getAmount();
        String sourceCurrency = transaction.getCurrency();

        for (String targetCurrency : TARGET_CURRENCIES) {
            BigDecimal convertedAmount = convertCurrency(amount, sourceCurrency, targetCurrency, exchangeRates);
            convertedAmounts.put(targetCurrency, convertedAmount);
        }

        transaction.setConvertedAmounts(convertedAmounts);
        return transactionRepository.save(transaction);
    }

    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, Map<String, BigDecimal> exchangeRates) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal fromRate = exchangeRates.get(fromCurrency);
        BigDecimal toRate = exchangeRates.get(toCurrency);

        return amount.divide(fromRate, 6, RoundingMode.HALF_UP).multiply(toRate);
    }

    public List<Transaction> getTransactions(String userId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);
    }

    private record ExchangeRateResponse(Map<String, BigDecimal> rates) {}
}