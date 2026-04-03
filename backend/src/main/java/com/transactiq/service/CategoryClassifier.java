package com.transactiq.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Rule-based merchant → category classifier.
 * Extend with an AI/ML model for production use.
 */
@Service
public class CategoryClassifier {

    private static final Map<String, String> MERCHANT_RULES = Map.ofEntries(
        // Food & Dining
        Map.entry("starbucks", "Food & Dining"),
        Map.entry("mcdonald", "Food & Dining"),
        Map.entry("chipotle", "Food & Dining"),
        Map.entry("doordash", "Food & Dining"),
        Map.entry("uber eats", "Food & Dining"),
        Map.entry("grubhub", "Food & Dining"),
        Map.entry("whole foods", "Food & Dining"),
        Map.entry("trader joe", "Food & Dining"),
        Map.entry("subway", "Food & Dining"),
        Map.entry("dunkin", "Food & Dining"),
        // Shopping
        Map.entry("amazon", "Shopping"),
        Map.entry("target", "Shopping"),
        Map.entry("walmart", "Shopping"),
        Map.entry("best buy", "Shopping"),
        Map.entry("nike", "Shopping"),
        Map.entry("zara", "Shopping"),
        Map.entry("h&m", "Shopping"),
        // Transport
        Map.entry("uber", "Transport"),
        Map.entry("lyft", "Transport"),
        Map.entry("shell", "Transport"),
        Map.entry("chevron", "Transport"),
        Map.entry("metro", "Transport"),
        Map.entry("parking", "Transport"),
        // Entertainment
        Map.entry("netflix", "Entertainment"),
        Map.entry("spotify", "Subscriptions"),
        Map.entry("apple tv", "Entertainment"),
        Map.entry("amc", "Entertainment"),
        Map.entry("steam", "Entertainment"),
        Map.entry("hulu", "Entertainment"),
        // Health
        Map.entry("cvs", "Health"),
        Map.entry("walgreens", "Health"),
        Map.entry("gym", "Health"),
        Map.entry("planet fitness", "Health"),
        // Subscriptions
        Map.entry("adobe", "Subscriptions"),
        Map.entry("github", "Subscriptions"),
        Map.entry("aws", "Subscriptions"),
        Map.entry("google one", "Subscriptions"),
        Map.entry("icloud", "Subscriptions"),
        // Travel
        Map.entry("delta", "Travel"),
        Map.entry("united", "Travel"),
        Map.entry("marriott", "Travel"),
        Map.entry("airbnb", "Travel"),
        Map.entry("expedia", "Travel"),
        Map.entry("hertz", "Travel")
    );

    public String classify(String merchant) {
        if (merchant == null) return "Other";
        String lower = merchant.toLowerCase();
        for (Map.Entry<String, String> rule : MERCHANT_RULES.entrySet()) {
            if (lower.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        return "Other";
    }
}
