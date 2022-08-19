package com.github.datnguyenzzz.Actions;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;
import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Components.QuartzScheduler;

@Component
public class HealthyStatusAggregationReport {
    
    @Autowired
    private CronJobConfiguration config;

    @Autowired
    private QuartzScheduler scheduler;

    @Autowired
    private QuartzJobGenerator jobGenerator;

    private int healthCheckTimeWindow;

    @PostConstruct
    public void init() {
        this.healthCheckTimeWindow = config.getHealthCheckTimeWindow();
    }

    /**
     * @implNote Submit a job, in which perform InfluxDB aggregate every healthCheckTimeWindow sec
     */
    public void start() throws SchedulerException {
        JobDetail aggregateJobDetail = this.jobGenerator.genStatusAggregateJob(healthCheckTimeWindow);
        Trigger aggregateJobTrigger = null;
        this.scheduler.scheduleJob(aggregateJobDetail, aggregateJobTrigger);
    }

}