package com.example.analytics;

import com.example.analytics.records.Order;
import com.example.analytics.records.Product;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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
        
        // Test the new BigDecimalStatistics method
        System.out.println("\nTesting getProductValueStatistics...");
        // Generate some products
        List<Product> products = DataGenerator.generateProducts(100);
        BigDecimalStatistics stats = analyticsService.getProductValueStatistics(products);
        
        System.out.println("Product Price Statistics:");
        System.out.println("Count: " + stats.count());
        System.out.println("Sum: " + stats.sum());
        System.out.println("Min: " + stats.min());
        System.out.println("Max: " + stats.max());
        System.out.println("Average: " + stats.average());
        
        // Test with empty list
        System.out.println("\nTesting with empty product list...");
        BigDecimalStatistics emptyStats = analyticsService.getProductValueStatistics(List.of());
        System.out.println("Empty list statistics:");
        System.out.println("Count: " + emptyStats.count());
        System.out.println("Sum: " + emptyStats.sum());
        System.out.println("Min: " + emptyStats.min());
        System.out.println("Max: " + emptyStats.max());
        System.out.println("Average: " + emptyStats.average());
        
        // Run performance benchmarks
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PERFORMANCE BENCHMARKS");
        System.out.println("=".repeat(50));
        
        // Create a larger dataset for performance testing
        List<Order> largeOrders = DataGenerator.generateOrders(50000);
        
        // Test sequential vs parallel
        PerformanceTester.benchmarkSequentialVsParallel(analyticsService, largeOrders);
        
        // Test different sizes
        PerformanceTester.benchmarkDifferentSizes(analyticsService, new DataGenerator());
        
        // Demonstrate thread safety issues
        PerformanceTester.demonstrateThreadSafetyIssue();
        
        // Compare ArrayList vs LinkedList
        PerformanceTester.benchmarkArrayListVsLinkedList(analyticsService, new DataGenerator());
    }
}
