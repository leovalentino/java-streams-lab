package com.example.analytics;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class BigDecimalCollectors {
    
    // Private accumulator class
    private static class BigDecimalAccumulator {
        private long count = 0L;
        private BigDecimal sum = BigDecimal.ZERO;
        private BigDecimal min = null;
        private BigDecimal max = null;
        
        // Add a value to the accumulator
        void accept(BigDecimal value) {
            if (value == null) {
                return;
            }
            count++;
            sum = sum.add(value);
            
            if (min == null || value.compareTo(min) < 0) {
                min = value;
            }
            if (max == null || value.compareTo(max) > 0) {
                max = value;
            }
        }
        
        // Combine two accumulators
        BigDecimalAccumulator combine(BigDecimalAccumulator other) {
            if (other.count == 0) {
                return this;
            }
            if (this.count == 0) {
                return other;
            }
            
            BigDecimalAccumulator result = new BigDecimalAccumulator();
            result.count = this.count + other.count;
            result.sum = this.sum.add(other.sum);
            
            // Determine min
            if (this.min == null) {
                result.min = other.min;
            } else if (other.min == null) {
                result.min = this.min;
            } else {
                result.min = this.min.compareTo(other.min) < 0 ? this.min : other.min;
            }
            
            // Determine max
            if (this.max == null) {
                result.max = other.max;
            } else if (other.max == null) {
                result.max = this.max;
            } else {
                result.max = this.max.compareTo(other.max) > 0 ? this.max : other.max;
            }
            
            return result;
        }
        
        // Finish to create BigDecimalStatistics
        BigDecimalStatistics finish() {
            if (count == 0) {
                return BigDecimalStatistics.NEUTRAL;
            }
            return BigDecimalStatistics.of(count, sum, min, max);
        }
    }
    
    // Public static method to create the collector
    public static <T> Collector<T, ?, BigDecimalStatistics> toBigDecimalStatistics(
            Function<? super T, BigDecimal> mapper) {
        
        return new Collector<T, BigDecimalAccumulator, BigDecimalStatistics>() {
            @Override
            public Supplier<BigDecimalAccumulator> supplier() {
                return BigDecimalAccumulator::new;
            }
            
            @Override
            public BiConsumer<BigDecimalAccumulator, T> accumulator() {
                return (acc, element) -> {
                    BigDecimal value = mapper.apply(element);
                    acc.accept(value);
                };
            }
            
            @Override
            public BinaryOperator<BigDecimalAccumulator> combiner() {
                return BigDecimalAccumulator::combine;
            }
            
            @Override
            public Function<BigDecimalAccumulator, BigDecimalStatistics> finisher() {
                return BigDecimalAccumulator::finish;
            }
            
            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
            }
        };
    }
}
