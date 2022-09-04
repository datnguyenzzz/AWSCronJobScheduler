package com.github.datnguyenzzz.Jobs;

import javax.annotation.PostConstruct;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Factories.JobExecuterFactory;
import com.github.datnguyenzzz.Interfaces.JobExecuter;
import com.github.datnguyenzzz.Services.QuartzJobGeneratorService;

/**
 * @apiNote Multiple instances of the job will not be allowed to run concurrently 
 * (consider a case where a job has code in its execute() method that takes 34 seconds to run, 
 * but it is scheduled with a trigger that repeats every 30 seconds)
 * 
 * @apiNote JobDataMap contents re-persisted in the scheduler's JobStore after each execution.
 */
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PublishingJob implements CronJob {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

    private JobExecuterFactory jobExecuterFactory;

    @PostConstruct
    public void init() {
        this.jobExecuterFactory = (JobExecuterFactory) ctx.getBean(JobExecuterFactory.class);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        //get appropiate publisher
        try {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            String usedService = jobDetail.getDescription();

            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            // TODO: Trigger AWS publisher
            JobExecuter publisher = this.jobExecuterFactory.getObject(usedService);
            publisher.execute(jobDetail);

            // update job completed
            this.quartzJobGeneratorService.addHSJobComplete(dataMap);
        }
        catch (Exception ex) {
            // update job failed
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            this.quartzJobGeneratorService.addHSJobFailed(dataMap);
        }
    }
    
}
