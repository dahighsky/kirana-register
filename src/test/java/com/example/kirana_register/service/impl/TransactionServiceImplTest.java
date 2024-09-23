package com.example.kirana_register.service.impl;

import com.example.kirana_register.client.FxRatesClient;
import com.example.kirana_register.dto.request.TransactionRequest;
import com.example.kirana_register.dto.response.TransactionResponse;
import com.example.kirana_register.model.Transaction;
import com.example.kirana_register.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FxRatesClient fxRatesClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Map<String, BigDecimal> mockExchangeRates;

    @BeforeEach
    void setUp() {
        mockExchangeRates = FxRatesMockData.createMockExchangeRates();
    }

    @Test
    void getExchangeRates_shouldReturnRatesFromClient() {
        when(fxRatesClient.getExchangeRates()).thenReturn(mockExchangeRates);

        Map<String, BigDecimal> result = transactionService.getExchangeRates();

        assertEquals(mockExchangeRates, result);
        verify(fxRatesClient, times(1)).getExchangeRates();
    }

    @Test
    void recordTransaction_shouldSaveAndReturnTransactionResponse() {
        TransactionRequest request = TransactionMockData.createMockTransactionRequest();
        Transaction mockTransaction = TransactionMockData.createMockTransaction();

        when(fxRatesClient.getExchangeRates()).thenReturn(mockExchangeRates);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        TransactionResponse response = transactionService.recordTransaction(request);

        assertNotNull(response);
        assertEquals(mockTransaction.getId(), response.getTransactionId());
        assertEquals(mockTransaction.getUserId(), response.getUserId());
        assertTrue(mockTransaction.getAmount().compareTo(response.getAmount()) == 0,
                "Expected amount: " + mockTransaction.getAmount() + ", Actual amount: " + response.getAmount());
        assertEquals(mockTransaction.getCurrency(), response.getCurrency());
        assertEquals(mockTransaction.getType(), response.getType());
        assertNotNull(response.getTimestamp());

        // Compare converted amounts
        assertEquals(mockTransaction.getConvertedAmounts().size(), response.getConvertedAmounts().size());
        for (Map.Entry<String, BigDecimal> entry : mockTransaction.getConvertedAmounts().entrySet()) {
            String currency = entry.getKey();
            BigDecimal expectedAmount = entry.getValue();
            BigDecimal actualAmount = response.getConvertedAmounts().get(currency);
            assertTrue(expectedAmount.compareTo(actualAmount) == 0,
                    "For currency " + currency + ", expected: " + expectedAmount + ", actual: " + actualAmount);
        }

        verify(fxRatesClient, times(1)).getExchangeRates();
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactions_shouldReturnTransactionsForUserInTimeRange() {
        String userId = "user1";
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        List<Transaction> mockTransactions = TransactionMockData.createMockTransactionList();

        when(transactionRepository.findByUserIdAndTimestampBetween(userId, start, end)).thenReturn(mockTransactions);

        List<TransactionResponse> responses = transactionService.getTransactions(userId, start, end);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        for (int i = 0; i < responses.size(); i++) {
            TransactionResponse response = responses.get(i);
            Transaction mockTransaction = mockTransactions.get(i);

            assertEquals(mockTransaction.getId(), response.getTransactionId());
            assertEquals(mockTransaction.getUserId(), response.getUserId());
            assertTrue(mockTransaction.getAmount().compareTo(response.getAmount()) == 0,
                    "For transaction " + i + ", expected amount: " + mockTransaction.getAmount() + ", actual amount: " + response.getAmount());
            assertEquals(mockTransaction.getCurrency(), response.getCurrency());
            assertEquals(mockTransaction.getType(), response.getType());
            assertEquals(mockTransaction.getTimestamp(), response.getTimestamp());

            // Compare converted amounts
            assertEquals(mockTransaction.getConvertedAmounts().size(), response.getConvertedAmounts().size());
            for (Map.Entry<String, BigDecimal> entry : mockTransaction.getConvertedAmounts().entrySet()) {
                String currency = entry.getKey();
                BigDecimal expectedAmount = entry.getValue();
                BigDecimal actualAmount = response.getConvertedAmounts().get(currency);
                assertTrue(expectedAmount.compareTo(actualAmount) == 0,
                        "For transaction " + i + ", currency " + currency + ", expected: " + expectedAmount + ", actual: " + actualAmount);
            }
        }

        verify(transactionRepository, times(1)).findByUserIdAndTimestampBetween(userId, start, end);
    }
}