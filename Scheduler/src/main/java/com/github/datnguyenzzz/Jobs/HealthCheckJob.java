package com.github.datnguyenzzz.Jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.HealthCheckEngine;

@Component
@Scope("prototype")
public class HealthCheckJob implements Job {
    
    @Autowired
    private HealthCheckEngine healthCheckEngine;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        healthCheckEngine.run();
    }
    
}
