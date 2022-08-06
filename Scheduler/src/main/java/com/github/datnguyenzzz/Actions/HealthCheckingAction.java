package com.github.datnguyenzzz.Actions;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;
import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Components.QuartzScheduler;

@Component
public class HealthCheckingAction {

    private final static String JOB_TRIGGER = "jobTrigger";

    @Autowired
    private QuartzScheduler scheduler;

    @Autowired
    private QuartzJobGenerator jobGenerator;

    @Autowired
    private CronJobConfiguration config;

    public void start() throws SchedulerException {

        int healthCheckFrequency = config.getHealthCheckFrequency();
        // init health check job
        JobDetail jobDetail = jobGenerator.genHealthCheckJobDetail(healthCheckFrequency);
        // get trigger pack within
        Trigger jobTrigger = (Trigger) jobDetail.getJobDataMap().get(JOB_TRIGGER);

        // schedule job
        this.scheduler.scheduleJob(jobDetail, jobTrigger);        
    }
}
