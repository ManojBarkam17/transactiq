package com.transactiq.service;

import com.transactiq.model.Transaction;
import com.transactiq.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InsightService Tests")
class InsightServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private InsightService insightService;

    @Test
    @DisplayName("Should build transaction summary for AI analysis")
    void buildTransactionSummary_withTransactions_returnsFormattedSummary() {
        List<Transaction> transactions = List.of(
                createTransaction("Starbucks", "5.75", "Food & Dining", "2024-03-15"),
                createTransaction("Amazon", "34.99", "Shopping", "2024-03-14"),
                createTransaction("Uber", "12.50", "Transport", "2024-03-13"),
                createTransaction("Netflix", "15.99", "Entertainment", "2024-03-12")
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        String summary = insightService.buildTransactionSummary();

        assertThat(summary).contains("Food & Dining");
        assertThat(summary).contains("Shopping");
        assertThat(summary).contains("Transport");
        assertThat(summary).isNotBlank();
    }

    @Test
    @DisplayName("Should handle empty transaction list gracefully")
    void buildTransactionSummary_noTransactions_returnsEmptyMessage() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        String summary = insightService.buildTransactionSummary();

        assertThat(summary).contains("No transactions");
    }

    @Test
    @DisplayName("Should calculate total spending correctly")
    void calculateTotalSpending_multipleTransactions_sumsCorrectly() {
        List<Transaction> transactions = List.of(
                createTransaction("Store A", "10.00", "Shopping", "2024-03-15"),
                createTransaction("Store B", "20.50", "Shopping", "2024-03-14"),
                createTransaction("Store C", "5.25", "Food", "2024-03-13")
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        BigDecimal total = insightService.calculateTotalSpending();

        assertThat(total).isEqualByComparingTo(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Should identify top spending category")
    void getTopCategory_multipleCategories_returnsHighestSpend() {
        List<Transaction> transactions = List.of(
                createTransaction("Starbucks", "50.00", "Food & Dining", "2024-03-15"),
                createTransaction("Amazon", "200.00", "Shopping", "2024-03-14"),
                createTransaction("Target", "150.00", "Shopping", "2024-03-13"),
                createTransaction("Uber", "30.00", "Transport", "2024-03-12")
        );

        when(transactionRepository.findAll()).thenReturn(transactions);

        String topCategory = insightService.getTopSpendingCategory();

        assertThat(topCategory).isEqualTo("Shopping");
    }

    private Transaction createTransaction(String merchant, String amount,
                                          String category, String date) {
        Transaction t = new Transaction();
        t.setMerchant(merchant);
        t.setAmount(new BigDecimal(amount));
        t.setCategory(category);
        t.setDate(LocalDate.parse(date));
        return t;
    }
}
