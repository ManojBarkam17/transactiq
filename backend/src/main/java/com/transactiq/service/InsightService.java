package com.transactiq.service;

import com.transactiq.model.Transaction;
import com.transactiq.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * Integrates with the Anthropic Claude API to generate natural-language
 * spending insights from a user's transaction history.
 */
@Service
public class InsightService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.model:claude-opus-4-6}")
    private String model;

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public InsightService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public String generateInsights(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        if (transactions.isEmpty()) {
            return "No transactions found. Upload your bank statement CSV to get started.";
        }

        String transactionSummary = buildTransactionSummary(transactions);
        String prompt = buildPrompt(transactionSummary);

        return callClaudeApi(prompt);
    }

    private String buildTransactionSummary(List<Transaction> transactions) {
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            categoryTotals.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
            total = total.add(t.getAmount());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total transactions: ").append(transactions.size()).append("\n");
        sb.append("Total spend: $").append(total.setScale(2, java.math.RoundingMode.HALF_UP)).append("\n\n");
        sb.append("Spending by category:\n");
        categoryTotals.forEach((cat, amt) ->
            sb.append("- ").append(cat).append(": $").append(amt.setScale(2, java.math.RoundingMode.HALF_UP)).append("\n")
        );

        // Add top 10 transactions
        sb.append("\nTop recent transactions:\n");
        transactions.stream().limit(10).forEach(t ->
            sb.append("- ").append(t.getDate()).append(" | ").append(t.getMerchant())
              .append(" | $").append(t.getAmount()).append("\n")
        );

        return sb.toString();
    }

    private String buildPrompt(String transactionSummary) {
        return """
            You are a personal finance AI assistant. Analyze the following transaction data and provide
            5 actionable, specific insights. Focus on:
            1. Unusual spending spikes
            2. Recurring patterns (subscriptions, habits)
            3. Savings opportunities with dollar estimates
            4. Category trends
            5. Behavioral patterns (weekend vs weekday, etc.)

            Format each insight as: [EMOJI] [Title]: [2-3 sentence explanation with specific numbers]

            Transaction Data:
            """ + transactionSummary;
    }

    private String callClaudeApi(String prompt) {
        String url = "https://api.anthropic.com/v1/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1024);
        body.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            return "AI analysis unavailable: " + e.getMessage();
        }
    }
}
