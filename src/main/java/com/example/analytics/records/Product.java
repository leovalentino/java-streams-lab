package com.example.analytics.records;

import java.math.BigDecimal;

public record Product(
    String id,
    String name,
    String category,
    BigDecimal price,
    Integer stockQuantity
) {
    public Product {
        if (price == null) {
            price = BigDecimal.ZERO;
        }
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
    }
}
