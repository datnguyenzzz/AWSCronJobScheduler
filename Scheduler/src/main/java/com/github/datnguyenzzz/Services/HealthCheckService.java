package com.github.datnguyenzzz.Services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.NotMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.HealthStatus;
import com.github.datnguyenzzz.Listeners.JobHealthyStatusUpdateJobListener;
import com.github.datnguyenzzz.Listeners.JobHealthyStatusUpdateTriggerListener;

/**
 * Handle everytime health check job finish
 */
@Service
@Scope("singleton")
public class HealthCheckService {

    private Map<Integer, HealthStatus> allHealthStatus;

    @Autowired
    private AddJobServiceImpl addJobService;

    @Autowired
    private SchedulerEngineDistributionHandlerService schedulerEngineService;

    @Autowired
    private JobHealthyStatusUpdateTriggerListener healthyTriggerListener;

    @Autowired
    private JobHealthyStatusUpdateJobListener healthyJobListener;

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    @PostConstruct
    public void init() {
        this.allHealthStatus = new HashMap<>();
        healthyTriggerListener.setName("Healthy update with trigger");
        healthyJobListener.setName("Healthy update with job");
    }

    /**
     * @apiNote Muttable api
     */
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

    /**
     * @apiNote Add health status listener into all scheduler engine
     */
    public void updateListenerToUpdateHealthStatus() throws SchedulerException {
        List<SchedulerEngine> allEngines = this.schedulerEngineService.getAllSchedulerEngines();

        for (SchedulerEngine engine: allEngines) {
            this.addJobService.addTriggerListener(engine, 
                    healthyTriggerListener, 
                    NotMatcher.not(GroupMatcher.triggerGroupEquals(HEALTH_CHECK_GROUP)));

            this.addJobService.addJobListener(engine,
                    healthyJobListener, 
                    NotMatcher.not(GroupMatcher.jobGroupEquals(HEALTH_CHECK_GROUP)));
        }
    }

}
