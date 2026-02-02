package com.example.analytics.records;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
    UUID id,
    Customer customer,
    LocalDateTime orderDate,
    List<Transaction> transactions,
    String status
) {
    public Order {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (transactions == null) {
            transactions = List.of();
        }
    }
}
