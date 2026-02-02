package com.example.analytics;

import com.example.analytics.records.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class AnalyticsServiceImpl implements AnalyticsService {
    
    @Override
    public List<Customer> topCustomersBySpendInMonth(List<Order> orders, YearMonth yearMonth) {
        // Map to store customer ID to total spend
        Map<String, BigDecimal> customerSpendMap = new HashMap<>();
        Map<String, Customer> customerMap = new HashMap<>();
        
        // Process each order
        for (Order order : orders) {
            // Skip orders with null customer
            if (order.customer() == null) {
                continue;
            }
            
            // Check if order is in the specified YearMonth
            LocalDateTime orderDate = order.orderDate();
            YearMonth orderYearMonth = YearMonth.from(orderDate);
            if (!orderYearMonth.equals(yearMonth)) {
                continue;
            }
            
            // Process transactions
            List<Transaction> transactions = order.transactions();
            if (transactions == null) {
                continue;
            }
            
            Customer customer = order.customer();
            String customerId = customer.id();
            
            // Store customer info
            customerMap.putIfAbsent(customerId, customer);
            
            // Calculate total spend for this order
            BigDecimal orderTotal = BigDecimal.ZERO;
            for (Transaction transaction : transactions) {
                // Skip transactions with null values
                if (transaction == null) {
                    continue;
                }
                // Add transaction total value
                BigDecimal transactionValue = transaction.totalValue();
                if (transactionValue != null) {
                    orderTotal = orderTotal.add(transactionValue);
                }
            }
            
            // Update customer's total spend
            BigDecimal currentTotal = customerSpendMap.getOrDefault(customerId, BigDecimal.ZERO);
            customerSpendMap.put(customerId, currentTotal.add(orderTotal));
        }
        
        // Convert map to list of entries for sorting
        List<Map.Entry<String, BigDecimal>> entries = new ArrayList<>(customerSpendMap.entrySet());
        
        // Sort in descending order by total spend
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        
        // Get top 3 customers
        List<Customer> topCustomers = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, BigDecimal> entry : entries) {
            if (count >= 3) {
                break;
            }
            Customer customer = customerMap.get(entry.getKey());
            if (customer != null) {
                topCustomers.add(customer);
                count++;
            }
        }
        
        return topCustomers;
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
