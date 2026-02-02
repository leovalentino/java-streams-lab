package com.example.analytics;

import com.example.analytics.records.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private static final String[] CATEGORIES = {
        "Electronics", "Clothing", "Books", "Home & Garden", "Sports", "Toys", "Automotive", "Health"
    };
    
    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
    private static final String[] STATUSES = {"SUCCESS", "FAILED", "PENDING", "CANCELLED"};
    private static final String[] CUSTOMER_TIERS = {"STANDARD", "PREMIUM", "VIP"};
    
    public static List<Order> generateOrders(int count) {
        List<Order> orders = new ArrayList<>();
        List<Product> products = generateProducts(50);
        List<Customer> customers = generateCustomers(100);
        
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < count; i++) {
            // Randomly decide to create edge cases
            boolean createEdgeCase = ThreadLocalRandom.current().nextDouble() < 0.05;
            
            UUID orderId = UUID.randomUUID();
            Customer customer = customers.get(ThreadLocalRandom.current().nextInt(customers.size()));
            
            // Sometimes use null customer for edge cases
            if (createEdgeCase && ThreadLocalRandom.current().nextBoolean()) {
                customer = null;
            }
            
            LocalDateTime orderDate = now.minusDays(ThreadLocalRandom.current().nextInt(365));
            
            // Generate random number of transactions (0-5)
            int transactionCount = ThreadLocalRandom.current().nextInt(6);
            List<Transaction> transactions = new ArrayList<>();
            
            for (int j = 0; j < transactionCount; j++) {
                Product product = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                int quantity = ThreadLocalRandom.current().nextInt(1, 11);
                BigDecimal unitPrice = product.price();
                
                // Sometimes use null price for edge cases
                if (createEdgeCase && ThreadLocalRandom.current().nextBoolean()) {
                    unitPrice = null;
                }
                
                LocalDateTime timestamp = orderDate.plusMinutes(ThreadLocalRandom.current().nextInt(60));
                String status = STATUSES[ThreadLocalRandom.current().nextInt(STATUSES.length)];
                
                Transaction transaction = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8),
                    product,
                    quantity,
                    unitPrice,
                    timestamp,
                    status
                );
                transactions.add(transaction);
            }
            
            // Sometimes create empty transaction list for edge cases
            if (createEdgeCase && ThreadLocalRandom.current().nextBoolean()) {
                transactions = List.of();
            }
            
            // Sometimes use null transaction list for edge cases
            if (createEdgeCase && ThreadLocalRandom.current().nextBoolean()) {
                transactions = null;
            }
            
            String orderStatus = STATUSES[ThreadLocalRandom.current().nextInt(STATUSES.length)];
            
            Order order = new Order(
                orderId,
                customer,
                orderDate,
                transactions,
                orderStatus
            );
            orders.add(order);
        }
        
        // Add some duplicate IDs for edge cases
        if (!orders.isEmpty()) {
            Order firstOrder = orders.get(0);
            orders.add(new Order(
                firstOrder.id(),
                firstOrder.customer(),
                firstOrder.orderDate(),
                firstOrder.transactions(),
                firstOrder.status()
            ));
        }
        
        return orders;
    }
    
    public static List<Product> generateProducts(int count) {
        List<Product> products = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id;
            do {
                id = "PROD-" + (1000 + i);
            } while (usedIds.contains(id));
            usedIds.add(id);
            
            String name = "Product " + (char) ('A' + (i % 26)) + (i / 26 + 1);
            String category = CATEGORIES[ThreadLocalRandom.current().nextInt(CATEGORIES.length)];
            BigDecimal price = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(10.0, 1000.0))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            Integer stockQuantity = ThreadLocalRandom.current().nextInt(0, 1001);
            
            products.add(new Product(id, name, category, price, stockQuantity));
        }
        return products;
    }
    
    private static List<Customer> generateCustomers(int count) {
        List<Customer> customers = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id;
            do {
                id = "CUST-" + (10000 + i);
            } while (usedIds.contains(id));
            usedIds.add(id);
            
            String firstName = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
            String name = firstName + " " + lastName;
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";
            String tier = CUSTOMER_TIERS[ThreadLocalRandom.current().nextInt(CUSTOMER_TIERS.length)];
            
            // Generate some random secondary emails (0-2)
            List<String> secondaryEmails = new ArrayList<>();
            int numSecondary = ThreadLocalRandom.current().nextInt(0, 3);
            for (int j = 0; j < numSecondary; j++) {
                secondaryEmails.add("secondary" + j + "." + lastName.toLowerCase() + "@example.com");
            }
            customers.add(new Customer(id, name, email, tier, secondaryEmails));
        }
        return customers;
    }
}
