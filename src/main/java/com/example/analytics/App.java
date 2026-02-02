package com.example.analytics;

import com.example.analytics.records.Order;
import java.time.YearMonth;
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
        
        // Test the AnalyticsService implementation
        System.out.println("\nTesting AnalyticsService implementation...");
        
        AnalyticsService analyticsService = new AnalyticsServiceImpl();
        
        // Get current year and month for testing
        YearMonth currentYearMonth = YearMonth.now();
        System.out.println("Finding top 3 customers for: " + currentYearMonth);
        
        List<com.example.analytics.records.Customer> topCustomers = 
            analyticsService.topCustomersBySpendInMonth(orders, currentYearMonth);
        
        System.out.println("Top " + topCustomers.size() + " customers:");
        for (int i = 0; i < topCustomers.size(); i++) {
            com.example.analytics.records.Customer customer = topCustomers.get(i);
            System.out.println((i + 1) + ". " + customer.name() + " (ID: " + customer.id() + 
                               ", Tier: " + customer.tier() + ")");
        }
        
        // Also test with a different month (e.g., one month ago)
        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        System.out.println("\nFinding top 3 customers for: " + previousYearMonth);
        
        List<com.example.analytics.records.Customer> previousTopCustomers = 
            analyticsService.topCustomersBySpendInMonth(orders, previousYearMonth);
        
        System.out.println("Top " + previousTopCustomers.size() + " customers:");
        for (int i = 0; i < previousTopCustomers.size(); i++) {
            com.example.analytics.records.Customer customer = previousTopCustomers.get(i);
            System.out.println((i + 1) + ". " + customer.name() + " (ID: " + customer.id() + 
                               ", Tier: " + customer.tier() + ")");
        }
        
        // Test the new method
        System.out.println("\nTesting firstAndLastTransactionOfHighestValueOrder...");
        Map<String, Object> result = analyticsService.firstAndLastTransactionOfHighestValueOrder(orders);
        
        System.out.println("Result:");
        result.forEach((key, value) -> {
            if (value instanceof Map) {
                System.out.println(key + ":");
                ((Map<?, ?>) value).forEach((k, v) -> System.out.println("  " + k + ": " + v));
            } else {
                System.out.println(key + ": " + value);
            }
        });
    }
}
