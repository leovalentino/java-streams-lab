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
            .filter(order -> order.customer() != null)
            .filter(order -> YearMonth.from(order.orderDate()).equals(yearMonth))
            .filter(order -> order.transactions() != null)
            .flatMap(order -> order.transactions().stream()
                .filter(transaction -> transaction != null)
                .map(transaction -> new AbstractMap.SimpleEntry<>(
                    order.customer(),
                    transaction.totalValue() != null ? transaction.totalValue() : BigDecimal.ZERO
                ))
            )
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(
                    Map.Entry::getValue,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ))
            .entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Double> averageTransactionValuePerCategory(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.transactions() != null)
                .flatMap(o -> o.transactions().stream())
                .filter(t -> t != null && t.product() != null && t.product().category() != null)
                .collect(Collectors.groupingBy(
                        t -> t.product().category(),
                        Collectors.teeing(
                                Collectors.counting(), // Downstream 1: Count transactions
                                Collectors.reducing(   // Downstream 2: Sum values
                                        BigDecimal.ZERO,
                                        t -> t.totalValue() != null ? t.totalValue() : BigDecimal.ZERO,
                                        BigDecimal::add
                                ),
                                // Merger: Apply the logic (threshold + calculation)
                                (count, sum) -> count > 50 ? sum.doubleValue() / count : null
                        )
                ))
                // Post-filter to remove categories that returned null (count <= 50)
                .entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public List<Customer> detectSlaBreaches(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.customer() != null && o.transactions() != null)
                // 1. Group transactions by customer
                .collect(Collectors.groupingBy(
                        Order::customer,
                        Collectors.flatMapping(
                                o -> o.transactions().stream()
                                        .filter(t -> t != null && "FAILED".equals(t.status())),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                // 2. Filter for customers who have at least 3 failures total
                .filter(entry -> entry.getValue().size() >= 3)
                // 3. Logic to check the 24-hour rolling window
                .filter(entry -> hasSlidingWindowBreach(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private boolean hasSlidingWindowBreach(List<Transaction> failures) {
        // Sort failures by timestamp
        failures.sort(Comparator.comparing(Transaction::timestamp));

        // Sliding window: Check if transaction 'i' and 'i+2' are within 24 hours
        for (int i = 0; i < failures.size() - 2; i++) {
            LocalDateTime start = failures.get(i).timestamp();
            LocalDateTime end = failures.get(i + 2).timestamp();

            if (java.time.Duration.between(start, end).toHours() <= 24) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> firstAndLastTransactionOfHighestValueOrder(List<Order> orders) {
        // TODO: Implement this method
        return Map.of();
    }
}
