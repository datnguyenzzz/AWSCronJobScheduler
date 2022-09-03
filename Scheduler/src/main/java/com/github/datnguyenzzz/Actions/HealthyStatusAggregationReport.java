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
import com.github.datnguyenzzz.Services.AddJobServiceImpl;
import com.github.datnguyenzzz.Services.QuartzJobGeneratorService;

@Component
public class HealthyStatusAggregationReport {

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Autowired
    private AddJobServiceImpl addJobService;
    
    @Autowired
    private CronJobConfiguration config;

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

        Trigger aggregateJobTrigger = (Trigger) dataMap.get(JOB_TRIGGER);
        this.addJobService.scheduleJob(aggregateJobDetail, aggregateJobTrigger);
    }

}
