package com.example.analytics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public record BigDecimalStatistics(
    long count,
    BigDecimal sum,
    BigDecimal min,
    BigDecimal max,
    BigDecimal average
) {
    // Neutral/zero instance for empty streams
    public static final BigDecimalStatistics NEUTRAL = new BigDecimalStatistics(
        0L,
        BigDecimal.ZERO,
        null,
        null,
        null
    );
    
    // Constructor that calculates average
    public BigDecimalStatistics {
        // Validate inputs
        Objects.requireNonNull(sum, "sum cannot be null");
        
        // Calculate average if count > 0
        if (count > 0) {
            // Use MathContext.DECIMAL128 for division
            average = sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL128);
        } else {
            average = null;
        }
    }
    
    // Helper method to create from components
    public static BigDecimalStatistics of(long count, BigDecimal sum, BigDecimal min, BigDecimal max) {
        return new BigDecimalStatistics(count, sum, min, max, null);
    }
}
