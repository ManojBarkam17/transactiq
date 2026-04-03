package com.transactiq.service;

import com.transactiq.model.Transaction;
import com.transactiq.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final CategoryClassifier categoryClassifier;

    public TransactionService(TransactionRepository repository, CategoryClassifier categoryClassifier) {
        this.repository = repository;
        this.categoryClassifier = categoryClassifier;
    }

    public List<Transaction> getAllTransactions(String userId) {
        return repository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Transaction> getByCategory(String userId, String category) {
        return repository.findByUserIdAndCategory(userId, category);
    }

    public List<Transaction> searchTransactions(String userId, String keyword) {
        return repository.searchByMerchant(userId, keyword);
    }

    public Transaction save(Transaction transaction) {
        // Auto-classify if category is missing
        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            String category = categoryClassifier.classify(transaction.getMerchant());
            transaction.setCategory(category);
        }
        return repository.save(transaction);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Parses a CSV file with columns: date,merchant,amount,category(optional)
     * Example row: 2024-03-15,Starbucks,5.75,Food & Dining
     */
    public int importFromCsv(MultipartFile file, String userId) {
        List<Transaction> toSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; } // skip header row
                String[] cols = line.split(",", -1);
                if (cols.length < 3) continue;

                Transaction t = new Transaction();
                t.setDate(LocalDate.parse(cols[0].trim()));
                t.setMerchant(cols[1].trim());
                t.setAmount(new BigDecimal(cols[2].trim()));
                t.setCategory(cols.length > 3 && !cols[3].isBlank()
                    ? cols[3].trim()
                    : categoryClassifier.classify(cols[1].trim()));
                t.setUserId(userId);
                toSave.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV: " + e.getMessage(), e);
        }
        repository.saveAll(toSave);
        return toSave.size();
    }

    public List<Map<String, Object>> getCategorySummary(String userId) {
        List<Object[]> raw = repository.getCategorySpendSummary(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("category", row[0]);
            entry.put("total", row[1]);
            result.add(entry);
        }
        return result;
    }
}
