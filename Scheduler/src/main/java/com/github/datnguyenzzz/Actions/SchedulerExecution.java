package com.github.datnguyenzzz.Actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component
public class SchedulerExecution {

    @Autowired
    private ApplicationContext ctx;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    public void start() {
        logger.info("Start scheduler execution ...");

        CronJobProvider provider = ctx.getBean("providerFactory", CronJobProvider.class);
        JobListDefinition jobList = provider.getDefinition();
        jobList.updateRelation();

        System.out.println(jobList.getJobExecutionOrder());
    }
}
