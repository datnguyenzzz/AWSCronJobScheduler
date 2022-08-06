package com.github.datnguyenzzz.Jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HealthCheckJob implements Job {

    private final static Logger logger = LoggerFactory.getLogger(HealthCheckJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // TODO run health check job. Invoke HealthyStatusComponent singleton.
        logger.info("HEALTH CHECK JOB IS GOIN TO EXECUTE !!!");
    }
    
}
