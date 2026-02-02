package com.example.analytics.records;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(
    String id,
    Product product,
    Integer quantity,
    BigDecimal unitPrice,
    LocalDateTime timestamp,
    String status
) {
    public Transaction {
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }
        if (quantity == null) {
            quantity = 0;
        }
        if (status == null) {
            status = "PENDING";
        }
    }
    
    public BigDecimal totalValue() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
