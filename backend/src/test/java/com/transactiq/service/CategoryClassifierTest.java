package com.transactiq.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryClassifier Tests")
class CategoryClassifierTest {

    private CategoryClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new CategoryClassifier();
    }

    @ParameterizedTest
    @DisplayName("Should classify known merchants correctly")
    @CsvSource({
        "Starbucks, Food & Dining",
        "McDonalds, Food & Dining",
        "Chipotle, Food & Dining",
        "Amazon, Shopping",
        "Walmart, Shopping",
        "Target, Shopping",
        "Uber, Transport",
        "Lyft, Transport",
        "Shell Gas, Transport",
        "Netflix, Entertainment",
        "Spotify, Entertainment",
        "Comcast, Utilities",
        "AT&T, Utilities"
    })
    void classify_knownMerchant_returnsCorrectCategory(String merchant, String expectedCategory) {
        String result = classifier.classify(merchant);
        assertThat(result).isEqualTo(expectedCategory);
    }

    @Test
    @DisplayName("Should return 'Other' for unknown merchants")
    void classify_unknownMerchant_returnsOther() {
        String result = classifier.classify("RandomStoreXYZ123");
        assertThat(result).isEqualTo("Other");
    }

    @Test
    @DisplayName("Should handle case-insensitive matching")
    void classify_caseInsensitive_matchesCorrectly() {
        assertThat(classifier.classify("STARBUCKS")).isEqualTo("Food & Dining");
        assertThat(classifier.classify("starbucks")).isEqualTo("Food & Dining");
        assertThat(classifier.classify("StarBucks Coffee")).isEqualTo("Food & Dining");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void classify_nullInput_returnsOther() {
        String result = classifier.classify(null);
        assertThat(result).isEqualTo("Other");
    }

    @Test
    @DisplayName("Should handle empty string input")
    void classify_emptyString_returnsOther() {
        String result = classifier.classify("");
        assertThat(result).isEqualTo("Other");
    }

    @Test
    @DisplayName("Should classify partial merchant name matches")
    void classify_partialMatch_classifiesCorrectly() {
        // Merchant names often include location suffixes
        assertThat(classifier.classify("Uber Trip #1234")).isEqualTo("Transport");
        assertThat(classifier.classify("AMAZON.COM*MARKETPLACE")).isEqualTo("Shopping");
        assertThat(classifier.classify("Netflix.com")).isEqualTo("Entertainment");
    }
}
