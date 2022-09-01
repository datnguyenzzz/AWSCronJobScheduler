package com.github.datnguyenzzz.Services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Entities.HealthStatus;

/**
 * Handle everytime health check job finish
 */
@Component
@Scope("singleton")
public class HealthCheckService {

    private Map<Integer, HealthStatus> allHealthStatus;

    @PostConstruct
    public void init() {
        this.allHealthStatus = new HashMap<>();
    }

    public void addToHashMap(int jobId, HealthStatus healthStatus) {
        this.allHealthStatus.put(jobId, healthStatus);
    }

    /**
     * 
     * @param jobId
     * @param time
     * @param jobName
     * @param jobFired
     * @param jobMisFired
     * @param jobCompleted
     * @param jobStatus
     * 
     * @apiNote Add health status dto to bean. Map[jobId] = DTO<status>
     */
    public void addToHashMap(int jobId, LocalDateTime time, String jobName, int jobFired, 
                             int jobMisFired, int jobCompleted, int jobFailed, String jobStatus) 
    {   
        HealthStatus healthStatus = new HealthStatus(time, jobName, jobFired, jobMisFired, jobCompleted, jobFailed, jobStatus);
        this.allHealthStatus.put(jobId, healthStatus);
    }

    public Map<Integer, HealthStatus> getAllHealthStatus() {
        return this.allHealthStatus;
    }

}
