package com.github.datnguyenzzz.Actions;

import javax.annotation.PostConstruct;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;
import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Services.QuartzJobGeneratorService;

@Component
public class HealthyStatusAggregationReport {

    @Value("${verbal.schedulerKey}")
    private String SCHEDULER_KEY;

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;
    
    @Autowired
    private CronJobConfiguration config;

    @Autowired
    private SchedulerEngine scheduler;

    @Autowired
    private QuartzJobGeneratorService jobGenerator;

    private int healthCheckTimeWindow;

    @PostConstruct
    public void init() {
        this.healthCheckTimeWindow = config.getHealthCheckTimeWindow();
    }

    /**
     * @implNote Submit a job, in which perform InfluxDB aggregate every healthCheckTimeWindow sec
     */
    public void start() throws SchedulerException {
        JobDetail aggregateJobDetail = this.jobGenerator.genStatusHealthCheckJob(healthCheckTimeWindow);

        JobDataMap dataMap = aggregateJobDetail.getJobDataMap();

        dataMap.put(SCHEDULER_KEY, this.scheduler);

        Trigger aggregateJobTrigger = (Trigger) dataMap.get(JOB_TRIGGER);
        this.scheduler.scheduleJob(aggregateJobDetail, aggregateJobTrigger);
    }

}
