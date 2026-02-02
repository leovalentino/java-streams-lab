package com.example.analytics.records;

public record Customer(
    String id,
    String name,
    String email,
    String tier
) {
    public Customer {
        if (tier == null) {
            tier = "STANDARD";
        }
    }
}
