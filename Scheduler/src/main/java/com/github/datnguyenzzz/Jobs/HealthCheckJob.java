package com.github.datnguyenzzz.Jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Services.SchedulerEngineDistributionHandlerService;

@Component
@PersistJobDataAfterExecution
public class HealthCheckJob implements CronJob {
    
    @Autowired
    private SchedulerEngineDistributionHandlerService schedulerEngineService;

    private final Logger logger = LoggerFactory.getLogger(HealthCheckJob.class);

    @Override
    public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
        // TODO Auto-generated method stub
        logger.info("HEALTH CHECKING !!!!!");

        try {
            this.schedulerEngineService.aggregateJobsHealthStatus();
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }
    
}
