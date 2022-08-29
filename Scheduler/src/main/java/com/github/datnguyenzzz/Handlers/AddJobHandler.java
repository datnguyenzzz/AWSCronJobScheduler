package com.github.datnguyenzzz.Handlers;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Actions.SequentialExecutionJobListener;
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

    @Autowired
    private ApplicationContext ctx;

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;
    
    /**
     * 
     * @param job
     * @apiNote Add new job to scheduler
     */
    public void addNewJob(AWSJob awsJob) throws SchedulerException {
        JobDetail jobDetail = this.jobGenerator.genPublishingJobDetail(awsJob);
        this.scheduler.addJob(jobDetail, false);
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobsExecuteNext
     * @apiNote Add trigger to handle sequential execution
     */
    public void addSequentialTrigger(AWSJob jobExecuteFirst, List<AWSJob> listJobsExecuteNext) throws SchedulerException {
        
        JobKey awsJobKeyFirst = this.jobGenerator.genJobKey(jobExecuteFirst);

        SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

        executionJobListener.setScheduler(this.scheduler);
        executionJobListener.setName(jobExecuteFirst.toString());

        // prepare list of next job
        for (AWSJob awsJobNext : listJobsExecuteNext) {
            JobKey awsJobNextKey = this.jobGenerator.genJobKey(awsJobNext);
            JobDetail awsJobDetail = this.getJobDetailFromKey(awsJobNextKey);

            executionJobListener.addToJobExecuteNext(awsJobDetail);
        }

        //add listener to Job that match JobKey
        this.addJobListener(executionJobListener, KeyMatcher.keyEquals(awsJobKeyFirst));
    }

    /**
     * 
     * @param jobListener
     * @param matcher
     * @throws SchedulerException
     * @apiNote immutable
     */
    public void addJobListener(JobListener jobListener, Matcher<JobKey> matcher) throws SchedulerException {
        this.scheduler.getListenerManager().addJobListener(jobListener, matcher);
    }

    /**
     * 
     * @param JobKey
     * @return JobDetail
     */
    public JobDetail getJobDetailFromKey(JobKey key) throws SchedulerException {
        JobDetail jobDetail = this.scheduler.getJobDetail(key);
        return jobDetail;
    }

    /**
     * 
     * @param awsjob
     * @apiNote schedule all job which current reside in scheduler
     */
    public void scheduleCurrentJob(AWSJob awsJob) throws SchedulerException {
        // find corresponding job detail stored in scheduler
        JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob);
        JobDetail awsJobDetail = this.scheduler.getJobDetail(awsJobKey);

        JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
        Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

        //Add jobNow to scheduler
        if (trigger != null) 
            this.scheduler.scheduleJob(trigger);
    }
}
