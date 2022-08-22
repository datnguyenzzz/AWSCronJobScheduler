package com.github.datnguyenzzz.Jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@PersistJobDataAfterExecution
public class HealthCheck  implements Job{

    private final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // TODO Auto-generated method stub
        logger.info("HEALTH CHECKING !!!!!");
    }
    
}
