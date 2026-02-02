package com.example.analytics;

import com.example.analytics.records.*;
import com.example.analytics.BigDecimalStatistics;
import com.example.analytics.BigDecimalCollectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.stream.Stream;

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
        record OrderValue(Order order, BigDecimal totalValue) {}

        return orders.stream()
                .filter(o -> o.transactions() != null && !o.transactions().isEmpty())
                .map(o -> {
                    BigDecimal sum = o.transactions().stream()
                            .filter(Objects::nonNull)
                            .map(t -> t.totalValue() != null ? t.totalValue() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new OrderValue(o, sum);
                })
                .max(Comparator.comparing(OrderValue::totalValue))
                .map(ov -> {
                    var validTs = ov.order().transactions().stream().filter(Objects::nonNull).toList();

                    // SequencedCollection magic
                    var first = validTs.getFirst();
                    var last = validTs.getLast();

                    return Map.of(
                            "orderId", ov.order().id().toString(),
                            "totalOrderValue", ov.totalValue(),
                            "firstTransaction", first,
                            "lastTransaction", last
                    );
                })
                .orElse(Map.of("message", "No valid orders found"));
    }
    
    @Override
    public BigDecimalStatistics getProductValueStatistics(List<Product> products) {
        return products.stream()
                .collect(BigDecimalCollectors.toBigDecimalStatistics(Product::price));
    }
    
    @Override
    public Map<UUID, BigDecimal> calculateComplexRiskScore(List<Order> orders) {
        return orders.stream()
                .filter(order -> order != null)
                .collect(Collectors.toMap(
                    Order::id,
                    order -> {
                        // Simulate CPU-intensive calculation
                        BigDecimal riskScore = BigDecimal.ZERO;
                        // Perform 1000 iterations of complex operations
                        for (int i = 0; i < 1000; i++) {
                            // Use various mathematical operations to simulate complexity
                            BigDecimal temp = BigDecimal.valueOf(Math.sin(order.id().getMostSignificantBits() + i));
                            temp = temp.multiply(BigDecimal.valueOf(Math.cos(order.id().getLeastSignificantBits() - i)));
                            temp = temp.pow(2);
                            riskScore = riskScore.add(temp.abs());
                        }
                        // Normalize the score
                        if (order.transactions() != null) {
                            int transactionCount = order.transactions().size();
                            riskScore = riskScore.divide(BigDecimal.valueOf(Math.max(1, transactionCount)), java.math.MathContext.DECIMAL128);
                        }
                        return riskScore;
                    }, (existingValue, newValue) -> existingValue
                ));
    }
    
    @Override
    public Map<LocalDate, BigDecimal> generateDateRangeReport(LocalDate start, LocalDate end) {
        // Use Stream.iterate to generate dates between start and end (inclusive)
        return Stream.iterate(start, date -> !date.isAfter(end), date -> date.plusDays(1))
                .collect(Collectors.toMap(
                    date -> date,
                    date -> {
                        // Calculate total sales for this date
                        return orders.stream()
                                .filter(order -> order != null && order.orderDate() != null)
                                .filter(order -> order.orderDate().toLocalDate().equals(date))
                                .flatMap(order -> order.transactions() != null ? 
                                         order.transactions().stream() : Stream.empty())
                                .filter(transaction -> transaction != null)
                                .map(transaction -> transaction.totalValue() != null ? 
                                     transaction.totalValue() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                ));
    }
    
    @Override
    public List<Order> getOrdersInPriceRangeSorted(List<Order> orders, BigDecimal min, BigDecimal max) {
        // First, sort orders by their total price
        List<Order> sortedOrders = orders.stream()
                .filter(order -> order != null)
                .sorted(Comparator.comparing(order -> 
                    order.transactions() != null ?
                    order.transactions().stream()
                            .filter(transaction -> transaction != null)
                            .map(transaction -> transaction.totalValue() != null ? 
                                 transaction.totalValue() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());
        
        // Now use dropWhile and takeWhile
        return sortedOrders.stream()
                .dropWhile(order -> {
                    BigDecimal orderTotal = order.transactions() != null ?
                            order.transactions().stream()
                                    .filter(transaction -> transaction != null)
                                    .map(transaction -> transaction.totalValue() != null ? 
                                         transaction.totalValue() : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                            : BigDecimal.ZERO;
                    return orderTotal.compareTo(min) < 0;
                })
                .takeWhile(order -> {
                    BigDecimal orderTotal = order.transactions() != null ?
                            order.transactions().stream()
                                    .filter(transaction -> transaction != null)
                                    .map(transaction -> transaction.totalValue() != null ? 
                                         transaction.totalValue() : BigDecimal.ZERO)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                            : BigDecimal.ZERO;
                    return orderTotal.compareTo(max) <= 0;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getCustomerEmails(Customer customer) {
        if (customer == null) {
            return List.of();
        }
        
        // Primary email stream using Stream.ofNullable
        Stream<String> primaryEmailStream = Stream.ofNullable(customer.email());
        
        // Secondary emails stream - using Stream.ofNullable to handle null
        // The Customer record now has secondaryEmails() method
        Stream<String> secondaryEmailStream = Stream.ofNullable(customer.secondaryEmails())
                .flatMap(List::stream);
        
        // Concatenate both streams and collect
        return Stream.concat(primaryEmailStream, secondaryEmailStream)
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
