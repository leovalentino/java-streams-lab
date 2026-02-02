package com.example.analytics;

import com.example.analytics.records.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.AbstractMap;

public class AnalyticsServiceImpl implements AnalyticsService {
    
    @Override
    public List<Customer> topCustomersBySpendInMonth(List<Order> orders, YearMonth yearMonth) {
        return orders.stream()
            // Filter out orders with null customer
            .filter(order -> order.customer() != null)
            // Filter orders in the specified YearMonth
            .filter(order -> YearMonth.from(order.orderDate()).equals(yearMonth))
            // Filter out orders with null transactions
            .filter(order -> order.transactions() != null)
            // Flatten each order into its transactions, preserving customer info
            .flatMap(order -> order.transactions().stream()
                // Filter out null transactions
                .filter(transaction -> transaction != null)
                // Map each transaction to a pair of customer and transaction value
                .map(transaction -> new AbstractMap.SimpleEntry<>(
                    order.customer(),
                    transaction.totalValue() != null ? transaction.totalValue() : BigDecimal.ZERO
                ))
            )
            // Group by customer and sum their transaction values
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Map.Entry::getValue,
                        BigDecimal::add
                    )
                )
            )
            // Get entry set stream
            .entrySet().stream()
            // Sort by total spend in descending order
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            // Take top 3
            .limit(3)
            // Extract the customer
            .map(Map.Entry::getKey)
            // Collect to list
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Double> averageTransactionValuePerCategory(List<Order> orders) {
        // TODO: Implement this method
        return Map.of();
    }
    
    @Override
    public List<Customer> detectSlaBreaches(List<Order> orders) {
        // TODO: Implement this method
        return List.of();
    }
    
    @Override
    public Map<String, Object> firstAndLastTransactionOfHighestValueOrder(List<Order> orders) {
        // TODO: Implement this method
        return Map.of();
    }
}
