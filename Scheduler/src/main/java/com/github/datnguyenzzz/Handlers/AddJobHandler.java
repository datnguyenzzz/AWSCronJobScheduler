package com.github.datnguyenzzz.Handlers;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Components.QuartzScheduler;
import com.github.datnguyenzzz.dto.AWSJob;

@Component
@Scope("prototype")
public class AddJobHandler {

    @Autowired
    private QuartzScheduler scheduler;

    @Autowired
    private QuartzJobGenerator jobGenerator;
    
    /**
     * 
     * @param job
     * @apiNote Add new job to scheduler
     */
    public void addNewJob(AWSJob awsJob) throws SchedulerException {
        JobDetail jobDetail = this.jobGenerator.genPublishingJobDetail(awsJob);
        this.scheduler.addJob(jobDetail, false);
    }
}
