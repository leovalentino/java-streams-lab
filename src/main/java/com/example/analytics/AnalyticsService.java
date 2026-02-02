package com.example.analytics;

import com.example.analytics.records.Order;
import com.example.analytics.records.Customer;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    
    // Intermediate: Find the top 3 customers by total spend in a specific YearMonth using flatMap and reduce
    List<Customer> topCustomersBySpendInMonth(List<Order> orders, YearMonth yearMonth);
    
    // Advanced: Group orders by Category, then find the average transaction value per category,
    // but only for categories with more than 50 orders (using collectingAndThen)
    Map<String, Double> averageTransactionValuePerCategory(List<Order> orders);
    
    // Advanced: Implement a "SLA Breach" detector that uses Stream.gatherers() (if enabled)
    // or complex groupingBy to find customers who had more than 2 failed transactions within a 24-hour window
    List<Customer> detectSlaBreaches(List<Order> orders);
    
    // Modern: Use SequencedCollections features alongside Streams to get the first and last transaction
    // of the highest-value order
    Map<String, Object> firstAndLastTransactionOfHighestValueOrder(List<Order> orders);
}
