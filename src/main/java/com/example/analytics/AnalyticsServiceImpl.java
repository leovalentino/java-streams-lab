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
            .collect(
                Collectors.teeing(
                    // First collector: group by customer and sum their transaction values
                    Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                            Map.Entry::getValue,
                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                    ),
                    // Second collector: collect all entries to pass to merger
                    // We can use Collectors.toList() but it's not used in the merger
                    // To be more efficient, we can use a dummy collector
                    Collectors.toList(),
                    // Merger: take the map, sort entries by value in descending order, take top 3, and extract customers
                    (customerTotalMap, unusedList) -> customerTotalMap.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
                )
            );
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
