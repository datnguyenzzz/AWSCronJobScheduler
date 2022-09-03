package com.github.datnguyenzzz.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.AWSJob;
import com.github.datnguyenzzz.Listeners.SequentialExecutionJobListener;

@Service
@Scope("prototype")
public class AddJobService {

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private SchedulerEngineDistributionHandlerService scheduleService;

    @Autowired
    private QuartzJobGeneratorService jobGenerator;

    /**
     * 
     * @param job
     * @apiNote Add new job to scheduler
     */
    public void addNewJob(AWSJob awsJob) throws SchedulerException {
        JobDetail jobDetail = this.jobGenerator.genPublishingJobDetail(awsJob);
        SchedulerEngine targetEngine = this.scheduleService.getAppropriateEngine();
        targetEngine.addJob(jobDetail, false);

        // store into repository
        JobKey jobKey = jobDetail.getKey();
        this.scheduleService.addJobKeyIntoRepo(jobKey, targetEngine);
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobsExecuteNext
     * @apiNote Add trigger to handle sequential execution of list of job
     */
    public void addSequentialTrigger(AWSJob jobExecuteFirst, List<AWSJob> listJobsExecuteNext) throws SchedulerException {
        
        JobKey awsJobKeyFirst = this.jobGenerator.genJobKey(jobExecuteFirst);
        List<JobKey> awsJobKeyListNext = listJobsExecuteNext.stream()
                                            .map((awsJob) -> this.jobGenerator.genJobKey(awsJob))
                                            .collect(Collectors.toList());

        SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

        //get engine where jobExecuteFirst reside
        SchedulerEngine targetEngine = this.scheduleService.getSchedulerEngineByJobKey(awsJobKeyFirst);
        executionJobListener.setName(awsJobKeyFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        for (JobKey jobExecuteNext: awsJobKeyListNext) {
            SchedulerEngine jobNextSchedulerEngine = this.scheduleService.getSchedulerEngineByJobKey(jobExecuteNext);
            JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(jobExecuteNext);
            executionJobListener.addToJobExecuteNext(awsJobDetailNext);
        }

        //add listener to target Engine
        this.addJobListener(targetEngine, executionJobListener, KeyMatcher.keyEquals(awsJobKeyFirst));
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobExecuteNext
     * @apiNote handle job sequential trigger
     */
    public void addSequentialTrigger(String jobExecuteFirst, AWSJob jobExecuteNext) throws SchedulerException {
        JobKey awsJobKeyFirst = this.jobGenerator.genJobKey(jobExecuteFirst);
        JobKey awsJobKeyNext = this.jobGenerator.genJobKey(jobExecuteNext);

        SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

        //get engine where jobExecuteFirst reside
        SchedulerEngine targetEngine = this.scheduleService.getSchedulerEngineByJobKey(awsJobKeyFirst);
        executionJobListener.setName(awsJobKeyFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        SchedulerEngine jobNextSchedulerEngine = this.scheduleService.getSchedulerEngineByJobKey(awsJobKeyNext);
        JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(awsJobKeyNext);

        executionJobListener.addToJobExecuteNext(awsJobDetailNext);

        //add listener to target Engine
        this.addJobListener(targetEngine, executionJobListener, KeyMatcher.keyEquals(awsJobKeyFirst));
    }

    /**
     * 
     * @param awsjob
     * @apiNote schedule all job which current reside in scheduler
     */
    public void scheduleCurrentJob(AWSJob awsJob) throws SchedulerException {
        // find corresponding job detail stored in scheduler
        JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob);

        //get engine where jobKey reside
        SchedulerEngine schedulerEngine = this.scheduleService.getSchedulerEngineByJobKey(awsJobKey);
        JobDetail awsJobDetail = schedulerEngine.getJobDetail(awsJobKey);

        JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
        Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

        //Add jobNow to scheduler
        if (trigger != null) 
            schedulerEngine.scheduleJob(trigger);
    }

    /**
     * 
     * @param jobDetail
     * @param trigger
     * @throws SchedulerException
     * @apiNote use 1 of schedule engines to schedule job 
     */
    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        //get appropriate scheduler
        SchedulerEngine schedulerEngine = this.scheduleService.getAppropriateEngine();
        schedulerEngine.scheduleJob(jobDetail, trigger);
    }

    /**
     * 
     * @param schedulerEngine
     * @param listener
     * @param matcher
     * 
     * @apiNote Add trigger listener into scheduler with particular matcher
     */
    public void addTriggerListener(SchedulerEngine schedulerEngine, 
        TriggerListener listener, Matcher<TriggerKey> matcher) throws SchedulerException
    {
        schedulerEngine.getListenerManager().addTriggerListener(listener, matcher);
    }

    /**
     * 
     * @param schedulerEngine
     * @param listener
     * @param matcher
     * @throws SchedulerException
     * 
     * @apiNote Add job listener into scheduler with particular matcher
     */
    public void addJobListener(SchedulerEngine schedulerEngine,
        JobListener listener, Matcher<JobKey> matcher) throws SchedulerException
    {
        schedulerEngine.getListenerManager().addJobListener(listener, matcher);
    }

}
