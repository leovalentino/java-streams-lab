package com.example.analytics;

import com.example.analytics.records.Order;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PerformanceTester {
    
    public static void benchmarkSequentialVsParallel(AnalyticsService service, List<Order> orders) {
        System.out.println("\n=== Performance Benchmark ===");
        System.out.println("Number of orders: " + orders.size());
        
        // Warm up the JVM with sequential stream (10 iterations)
        System.out.println("Warming up JVM with sequential stream...");
        for (int i = 0; i < 10; i++) {
            service.calculateComplexRiskScore(orders);
        }
        
        // Sequential execution
        System.out.println("Measuring sequential execution...");
        Instant seqStart = Instant.now();
        Map<UUID, BigDecimal> seqResult = service.calculateComplexRiskScore(orders);
        Instant seqEnd = Instant.now();
        long seqTime = Duration.between(seqStart, seqEnd).toMillis();
        System.out.println("Sequential time: " + seqTime + " ms");
        
        // Parallel execution
        System.out.println("Measuring parallel execution...");
        Instant parStart = Instant.now();
        Map<UUID, BigDecimal> parResult = service.calculateComplexRiskScore(
            orders.parallelStream().collect(Collectors.toList())
        );
        Instant parEnd = Instant.now();
        long parTime = Duration.between(parStart, parEnd).toMillis();
        System.out.println("Parallel time: " + parTime + " ms");
        
        // Speedup calculation
        double speedup = (double) seqTime / Math.max(parTime, 1);
        System.out.println("Speedup factor: " + String.format("%.2f", speedup));
        
        // Verify results are the same
        boolean resultsMatch = seqResult.equals(parResult);
        System.out.println("Results match: " + resultsMatch);
        if (!resultsMatch) {
            System.out.println("Warning: Sequential and parallel results differ!");
        }
    }
    
    public static void benchmarkDifferentSizes(AnalyticsService service, DataGenerator dataGenerator) {
        System.out.println("\n=== Benchmarking Different List Sizes ===");
        
        int[] sizes = {100, 10_000, 1_000_000};
        for (int size : sizes) {
            System.out.println("\n--- Size: " + size + " ---");
            List<Order> orders = dataGenerator.generateOrders(size);
            benchmarkSequentialVsParallel(service, orders);
        }
    }
    
    // Demonstrate thread-safety issues with a broken collector
    public static void demonstrateThreadSafetyIssue() {
        System.out.println("\n=== Demonstrating Thread Safety Issue ===");
        
        // Create a list of numbers
        List<BigDecimal> numbers = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            numbers.add(BigDecimal.valueOf(i + 1));
        }
        
        // Broken collector using non-thread-safe shared variable
        System.out.println("Testing broken collector with shared variable...");
        
        // Sequential execution (should be correct)
        BigDecimal[] sharedSumSeq = {BigDecimal.ZERO};
        numbers.stream()
               .forEach(n -> sharedSumSeq[0] = sharedSumSeq[0].add(n));
        BigDecimal expectedSum = numbers.stream()
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Sequential result: " + sharedSumSeq[0]);
        System.out.println("Expected sum: " + expectedSum);
        System.out.println("Sequential correct: " + sharedSumSeq[0].equals(expectedSum));
        
        // Parallel execution (likely incorrect due to race conditions)
        BigDecimal[] sharedSumPar = {BigDecimal.ZERO};
        numbers.parallelStream()
               .forEach(n -> sharedSumPar[0] = sharedSumPar[0].add(n));
        System.out.println("Parallel result: " + sharedSumPar[0]);
        System.out.println("Parallel correct: " + sharedSumPar[0].equals(expectedSum));
        
        // Fixed version using proper collector
        System.out.println("\nFixed version using proper collector:");
        BigDecimal parallelSum = numbers.parallelStream()
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Parallel sum with proper collector: " + parallelSum);
        System.out.println("Correct: " + parallelSum.equals(expectedSum));
    }
    
    public static void benchmarkArrayListVsLinkedList(AnalyticsService service, DataGenerator dataGenerator) {
        System.out.println("\n=== Benchmarking ArrayList vs LinkedList ===");
        
        List<Order> arrayList = dataGenerator.generateOrders(100_000);
        List<Order> linkedList = new LinkedList<>(arrayList);
        
        System.out.println("Testing with 100,000 orders...");
        
        // ArrayList sequential
        Instant start = Instant.now();
        service.calculateComplexRiskScore(arrayList);
        Instant end = Instant.now();
        System.out.println("ArrayList sequential: " + Duration.between(start, end).toMillis() + " ms");
        
        // ArrayList parallel
        start = Instant.now();
        service.calculateComplexRiskScore(arrayList.parallelStream().collect(Collectors.toList()));
        end = Instant.now();
        System.out.println("ArrayList parallel: " + Duration.between(start, end).toMillis() + " ms");
        
        // LinkedList sequential
        start = Instant.now();
        service.calculateComplexRiskScore(linkedList);
        end = Instant.now();
        System.out.println("LinkedList sequential: " + Duration.between(start, end).toMillis() + " ms");
        
        // LinkedList parallel
        start = Instant.now();
        service.calculateComplexRiskScore(linkedList.parallelStream().collect(Collectors.toList()));
        end = Instant.now();
        System.out.println("LinkedList parallel: " + Duration.between(start, end).toMillis() + " ms");
        
        System.out.println("\nExplanation:");
        System.out.println("Parallelism is significantly slower on a LinkedList due to the splitting overhead of the Spliterator.");
        System.out.println("ArrayList has O(1) random access, making it easy to split into balanced chunks.");
        System.out.println("LinkedList has O(n) traversal to find split points, causing imbalanced workloads");
        System.out.println("and high overhead in the fork-join framework.");
    }
}
