package com.github.datnguyenzzz.dto;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HealthyStatus {
    
    @Getter
    private final AtomicLong timeRange;

    @Getter
    private AtomicInteger successes = new AtomicInteger(0); 
    
    @Getter
    private AtomicInteger failures = new AtomicInteger(0);

    public int incrementSuccesses() {
        return this.successes.addAndGet(1);
    }

    public int incrementFailures() {
        return this.failures.addAndGet(1);
    }
}   
