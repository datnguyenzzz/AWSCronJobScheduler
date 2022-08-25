package com.github.datnguyenzzz.Components;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.dto.HealthStatus;

/**
 * Handle everytime health check job finish
 */
@Component
@Scope("Singleton")
public class HealthCheckHandler {

    private LocalDateTime time;
    
    private Map<Integer, HealthStatus> allHealthStatus;

    @PostConstruct
    public void init() {
        this.time = LocalDateTime.now();
        this.allHealthStatus = new HashMap<>();
    }

    public void addToHashMap(int jobId, HealthStatus healthStatus) {
        this.allHealthStatus.put(jobId, healthStatus);
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

}
