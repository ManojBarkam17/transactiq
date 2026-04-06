package com.transactiq.service;

import com.transactiq.model.Transaction;
import com.transactiq.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setMerchant("Starbucks");
        sampleTransaction.setAmount(new BigDecimal("5.75"));
        sampleTransaction.setCategory("Food & Dining");
        sampleTransaction.setDate(LocalDate.of(2024, 3, 15));
    }

    @Test
    @DisplayName("Should return all transactions")
    void getAllTransactions_returnsList() {
        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setMerchant("Amazon");
        t2.setAmount(new BigDecimal("34.99"));
        t2.setCategory("Shopping");

        when(transactionRepository.findAll()).thenReturn(List.of(sampleTransaction, t2));

        List<Transaction> result = transactionService.getAllTransactions();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMerchant()).isEqualTo("Starbucks");
    }

    @Test
    @DisplayName("Should save transaction successfully")
    void createTransaction_validData_saves() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        Transaction result = transactionService.createTransaction(sampleTransaction);

        assertThat(result.getMerchant()).isEqualTo("Starbucks");
        verify(transactionRepository).save(sampleTransaction);
    }

    @Test
    @DisplayName("Should delete transaction by ID")
    void deleteTransaction_existingId_deletes() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existent transaction deletion")
    void deleteTransaction_nonExistentId_throwsException() {
        when(transactionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.deleteTransaction(99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should calculate category summary correctly")
    void getCategorySummary_multipleTransactions_aggregatesCorrectly() {
        Transaction t1 = createTransaction("Starbucks", "5.75", "Food & Dining");
        Transaction t2 = createTransaction("McDonalds", "12.50", "Food & Dining");
        Transaction t3 = createTransaction("Amazon", "34.99", "Shopping");
        Transaction t4 = createTransaction("Uber", "18.00", "Transport");

        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2, t3, t4));

        Map<String, BigDecimal> summary = transactionService.getCategorySummary();

        assertThat(summary).containsEntry("Food & Dining", new BigDecimal("18.25"));
        assertThat(summary).containsEntry("Shopping", new BigDecimal("34.99"));
        assertThat(summary).containsEntry("Transport", new BigDecimal("18.00"));
        assertThat(summary).hasSize(3);
    }

    @Test
    @DisplayName("Should return empty summary for no transactions")
    void getCategorySummary_noTransactions_returnsEmptyMap() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        Map<String, BigDecimal> summary = transactionService.getCategorySummary();

        assertThat(summary).isEmpty();
    }

    private Transaction createTransaction(String merchant, String amount, String category) {
        Transaction t = new Transaction();
        t.setMerchant(merchant);
        t.setAmount(new BigDecimal(amount));
        t.setCategory(category);
        t.setDate(LocalDate.now());
        return t;
    }
}
