package com.example.kirana_register.service.impl;

import com.example.kirana_register.client.FxRatesClient;
import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.TransactionResponse;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import com.example.kirana_register.service.TransactionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final FxRatesClient fxRatesClient;

    private static final List<String> TARGET_CURRENCIES = List.of("INR", "USD", "EUR");

    public TransactionServiceImpl(TransactionRepository transactionRepository, FxRatesClient fxRatesClient) {
        this.transactionRepository = transactionRepository;
        this.fxRatesClient = fxRatesClient;
    }

    @Cacheable(value = "exchangeRates", key = "'latestRates'", sync = true)
    public Map<String, BigDecimal> getExchangeRates() {
        return fxRatesClient.getExchangeRates();
    }

    @Scheduled(fixedRate = 1000) // 1 second
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void clearExchangeRatesCache() {
        // Clear cache every 15 minutes
    }

    @Override
    public TransactionResponse recordTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setUserId(transactionRequest.getUserId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCurrency(transactionRequest.getCurrency());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setIsRefund(transactionRequest.getIsRefund());

        // Fetch exchange rates
        Map<String, BigDecimal> exchangeRates = getExchangeRates();
        Map<String, BigDecimal> convertedAmounts = new HashMap<>();
        BigDecimal amount = transaction.getAmount();
        String sourceCurrency = transaction.getCurrency();

        // Convert the transaction amounts
        for (String targetCurrency : TARGET_CURRENCIES) {
            BigDecimal convertedAmount = convertCurrency(amount, sourceCurrency, targetCurrency, exchangeRates);
            convertedAmounts.put(targetCurrency, convertedAmount);
        }

        transaction.setConvertedAmounts(convertedAmounts);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactions(String userId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse responseDTO = new TransactionResponse();
        responseDTO.setTransactionId(transaction.getId());
        responseDTO.setUserId(transaction.getUserId());
        responseDTO.setAmount(transaction.getAmount());
        responseDTO.setCurrency(transaction.getCurrency());
        responseDTO.setTimestamp(transaction.getTimestamp());
        responseDTO.setConvertedAmounts(transaction.getConvertedAmounts());
        return responseDTO;
    }

    private BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, Map<String, BigDecimal> exchangeRates) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal fromRate = exchangeRates.get(fromCurrency);
        BigDecimal toRate = exchangeRates.get(toCurrency);

        return amount.divide(fromRate, 6, RoundingMode.HALF_UP).multiply(toRate);
    }
}
