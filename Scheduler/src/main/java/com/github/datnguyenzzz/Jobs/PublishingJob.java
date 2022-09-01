package com.github.datnguyenzzz.Jobs;

import javax.annotation.PostConstruct;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Factories.AWSPublisherFactory;
import com.github.datnguyenzzz.Interfaces.AWSPublisher;
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
public class PublishingJob implements Job {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

    private AWSPublisherFactory awsPublisherFactory;

    @PostConstruct
    public void init() {
        this.awsPublisherFactory = ctx.getBean("awsPublisherFactory", AWSPublisherFactory.class);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        //get appropiate publisher
        try {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            String usedService = jobDetail.getDescription();

            String jobName = jobDetail.getKey().toString();
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            
            // TODO: Trigger AWS publisher
            AWSPublisher publisher = this.awsPublisherFactory.getObject(usedService);
            publisher.publish(jobName);

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
