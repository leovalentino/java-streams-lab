package com.example.analytics.records;

import java.util.List;

public record Customer(
    String id,
    String name,
    String email,
    String tier,
    List<String> secondaryEmails  // Added for Exercise 3
) {
    public Customer {
        if (tier == null) {
            tier = "STANDARD";
        }
        if (secondaryEmails == null) {
            secondaryEmails = List.of();
        }
    }
    
    // Additional constructor for backward compatibility
    public Customer(String id, String name, String email, String tier) {
        this(id, name, email, tier, List.of());
    }
}
