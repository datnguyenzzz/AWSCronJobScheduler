package com.github.datnguyenzzz.dto;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HealthyStatus {
    
    // atomic count for number of successes
    private AtomicInteger successes = new AtomicInteger(0); 
    
    // atomic count for number if failures
    private AtomicInteger failures = new AtomicInteger(0);

    public int addAndGetSuccesses() {
        return this.successes.addAndGet(1);
    }

    public int addAndGetFailures() {
        return this.failures.addAndGet(1);
    }

    public int getSuccesses() {
        return this.successes.get();
    }

    public int getFailures() {
        return this.failures.get();
    }
}   
