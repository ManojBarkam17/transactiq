package com.transactiq.controller;

import com.transactiq.model.Transaction;
import com.transactiq.service.InsightService;
import com.transactiq.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;
    private final InsightService insightService;

    public TransactionController(TransactionService transactionService, InsightService insightService) {
        this.transactionService = transactionService;
        this.insightService = insightService;
    }

    /**
     * Get all transactions for the current user
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam(defaultValue = "demo-user") String userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<Transaction> txns;
        if (search != null && !search.isBlank()) {
            txns = transactionService.searchTransactions(userId, search);
        } else if (category != null && !category.isBlank()) {
            txns = transactionService.getByCategory(userId, category);
        } else {
            txns = transactionService.getAllTransactions(userId);
        }
        return ResponseEntity.ok(txns);
    }

    /**
     * Create a single transaction
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.save(transaction));
    }

    /**
     * Upload CSV file of transactions
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "demo-user") String userId) {

        int count = transactionService.importFromCsv(file, userId);
        return ResponseEntity.ok(Map.of(
            "message", "Successfully imported " + count + " transactions",
            "count", count
        ));
    }

    /**
     * Get spending summary grouped by category
     */
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getSummary(
            @RequestParam(defaultValue = "demo-user") String userId) {
        return ResponseEntity.ok(transactionService.getCategorySummary(userId));
    }

    /**
     * Delete a transaction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Trigger AI analysis via Claude API
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeTransactions(
            @RequestParam(defaultValue = "demo-user") String userId) {
        String insights = insightService.generateInsights(userId);
        return ResponseEntity.ok(Map.of(
            "insights", insights,
            "userId", userId,
            "timestamp", System.currentTimeMillis()
        ));
    }
}
