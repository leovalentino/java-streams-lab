package com.example.analytics;

import com.example.analytics.records.Order;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("High-Frequency E-Commerce Analytics Engine");
        System.out.println("==========================================");
        
        // Generate sample data
        List<Order> orders = DataGenerator.generateOrders(1000);
        System.out.println("Generated " + orders.size() + " orders");
        
        // Count orders with edge cases
        long nullCustomerOrders = orders.stream()
            .filter(order -> order.customer() == null)
            .count();
        long emptyTransactions = orders.stream()
            .filter(order -> order.transactions() != null && order.transactions().isEmpty())
            .count();
        long nullTransactions = orders.stream()
            .filter(order -> order.transactions() == null)
            .count();
        
        System.out.println("Orders with null customer: " + nullCustomerOrders);
        System.out.println("Orders with empty transactions: " + emptyTransactions);
        System.out.println("Orders with null transactions: " + nullTransactions);
        
        // The AnalyticsService implementations will be added later
        System.out.println("\nReady for implementation of AnalyticsService methods.");
        System.out.println("Please implement the methods in a concrete class.");
    }
}
