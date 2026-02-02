# High-Frequency E-Commerce Analytics Engine

A Java 21 Streams lab project demonstrating advanced stream operations, custom collectors, performance benchmarking, and modern Java features.

## Overview

This project implements a simulated e-commerce analytics engine that processes orders, customers, products, and transactions using Java 21's latest features. It serves as a hands-on lab for practicing intermediate to advanced Stream API techniques.

## Project Structure

```
src/main/java/com/example/analytics/
├── records/                    # Java 21 Records for data model
│   ├── Order.java
│   ├── Customer.java
│   ├── Product.java
│   └── Transaction.java
├── AnalyticsService.java       # Interface defining analytics operations
├── AnalyticsServiceImpl.java   # Implementation using Streams
├── DataGenerator.java          # Generates test data with edge cases
├── BigDecimalStatistics.java   # Record for statistical calculations
├── BigDecimalCollectors.java   # Custom collector implementation
├── PerformanceTester.java      # Benchmarking utilities
└── App.java                    # Main entry point
```

## Java 21 Features Demonstrated

### 1. **Records (Java 16+)**
   - Immutable data carriers for `Order`, `Customer`, `Product`, and `Transaction`
   - Automatic `equals()`, `hashCode()`, `toString()` methods
   - Compact constructor syntax for validation

### 2. **Stream API (Java 8+)**
   - Intermediate operations: `filter()`, `map()`, `flatMap()`, `sorted()`
   - Terminal operations: `collect()`, `reduce()`, `forEach()`, `toList()`
   - Advanced collectors: `groupingBy()`, `teeing()`, `collectingAndThen()`

### 3. **Sequenced Collections (Java 21)**
   - `getFirst()` and `getLast()` methods on lists
   - Used in `firstAndLastTransactionOfHighestValueOrder()` method

### 4. **Custom Collectors**
   - `BigDecimalCollectors.toBigDecimalStatistics()`: Single-pass statistics collector
   - Thread-safe accumulator and combiner for parallel streams
   - Handles null values and empty streams gracefully

### 5. **Parallel Streams**
   - Performance comparison between sequential and parallel execution
   - Thread-safety demonstration with broken vs. fixed collectors
   - Data structure impact (ArrayList vs. LinkedList)

## Getting Started

### Prerequisites
- Java 21 or later
- Maven 3.6+

### Building and Running

```bash
# Compile the project
mvn compile

# Run the main application
mvn exec:java -Dexec.mainClass="com.example.analytics.App"

# Run with preview features enabled (if needed)
mvn compile -DcompilerArgs="--enable-preview"
```

## Lab Exercises

### 1. **Basic Stream Operations**
   - Explore `topCustomersBySpendInMonth()` method
   - Understand `flatMap()` for flattening nested collections
   - Practice with `groupingBy()` and `reducing()` collectors

### 2. **Advanced Collectors**
   - Study `averageTransactionValuePerCategory()` using `Collectors.teeing()`
   - Learn how to combine multiple downstream collectors
   - Implement filtering based on collected statistics

### 3. **Custom Collector Implementation**
   - Examine `BigDecimalCollectors.toBigDecimalStatistics()`
   - Understand the accumulator-combiner-finisher pattern
   - Test thread-safety with parallel streams

### 4. **Performance Benchmarking**
   - Run `PerformanceTester.benchmarkSequentialVsParallel()`
   - Compare ArrayList vs. LinkedList performance
   - Understand when parallel streams are beneficial

### 5. **Edge Case Handling**
   - Review `DataGenerator` for null values and empty collections
   - Test methods with generated edge cases
   - Implement defensive programming in stream pipelines

## Key Methods Explained

### `topCustomersBySpendInMonth()`
Finds the top 3 customers by total spend in a specific month using:
- `flatMap()` to flatten order transactions
- `groupingBy()` with `reducing()` to sum per customer
- `sorted()` and `limit()` for top-N selection

### `averageTransactionValuePerCategory()`
Groups transactions by product category and calculates average value only for categories with more than 50 orders:
- Uses `Collectors.teeing()` to count and sum simultaneously
- Applies threshold logic in the merger function
- Filters results post-collection

### `detectSlaBreaches()`
Identifies customers with more than 2 failed transactions within a 24-hour window:
- Groups failed transactions by customer
- Uses sliding window algorithm on sorted timestamps
- Demonstrates complex stateful stream operations

### `firstAndLastTransactionOfHighestValueOrder()`
Finds the highest-value order and returns its first and last transactions:
- Uses `Collectors.maxBy()` with custom comparator
- Employs Java 21's `getFirst()` and `getLast()` methods
- Returns structured data in a Map

### `calculateComplexRiskScore()`
Simulates CPU-intensive computation for performance testing:
- Performs 1000 mathematical operations per order
- Demonstrates when parallel streams are effective
- Used in performance benchmarks

## Performance Insights

### When to Use Parallel Streams
- **Good for**: CPU-intensive operations on large ArrayLists
- **Avoid for**: Small datasets or LinkedList data structures
- **Consider**: Overhead of thread coordination vs. computation time

### Thread Safety in Collectors
- **Broken**: Shared mutable state in `forEach()` causes race conditions
- **Fixed**: Proper accumulators with thread-safe combiners
- **Best Practice**: Always use stateless lambdas in parallel streams

### Data Structure Impact
- **ArrayList**: O(1) random access, efficient splitting for parallel streams
- **LinkedList**: O(n) traversal, poor parallel performance due to spliterator overhead

## Running Benchmarks

The application includes several benchmark tests:

```bash
# Run all benchmarks
mvn exec:java -Dexec.mainClass="com.example.analytics.App"

# Or run specific tests by modifying App.java
```

Benchmarks will show:
- Sequential vs. parallel execution times
- Speedup factors for different dataset sizes
- Thread-safety demonstrations
- ArrayList vs. LinkedList comparisons

## Extending the Lab

### Suggested Enhancements
1. Add more analytics methods using different Stream operations
2. Implement additional custom collectors for other statistics
3. Create JMH-based microbenchmarks for precise measurements
4. Add database persistence with JPA and stream processing
5. Implement reactive streams with Project Reactor

### Learning Resources
- [Java Stream API Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [Parallel Streams Guide](https://docs.oracle.com/en/java/javase/21/guides/parallelism/index.html)

## License

This project is for educational purposes. Feel free to use and modify for learning Java Streams and modern Java features.
