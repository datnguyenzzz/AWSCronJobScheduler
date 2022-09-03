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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.AWSJob;
import com.github.datnguyenzzz.Interfaces.AddJobService;
import com.github.datnguyenzzz.Listeners.SequentialExecutionJobListener;

@Service
@Scope("singleton")
public class AddJobServiceImpl implements AddJobService {

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private SchedulerEngineDistributionHandlerService scheduleService;

    @Autowired
    private QuartzJobGeneratorService jobGenerator;

    private final Logger logger = LoggerFactory.getLogger(AddJobServiceImpl.class);

    /**
     * 
     * @param job
     * @apiNote Add new job to scheduler
     */
    @Override
    public void addNewJob(AWSJob awsJob) {
        try {
            JobDetail jobDetail = this.jobGenerator.genPublishingJobDetail(awsJob);
            SchedulerEngine targetEngine = this.scheduleService.getAppropriateEngine();
            targetEngine.addJob(jobDetail, false);

            // store into repository
            JobKey jobKey = jobDetail.getKey();
            this.scheduleService.addJobKeyIntoRepo(jobKey, targetEngine);
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobsExecuteNext
     * @apiNote Add trigger to handle sequential execution of list of job
     */
    @Override
    public void addSequentialTrigger(AWSJob jobExecuteFirst, List<AWSJob> listJobsExecuteNext) {
        try {
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
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobExecuteNext
     * @apiNote handle job sequential trigger
     */
    @Override
    public void addSequentialTrigger(String jobExecuteFirst, AWSJob jobExecuteNext) {
        try {
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
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 
     * @param awsjob
     * @apiNote schedule all job which current reside in scheduler
     */
    @Override
    public void scheduleCurrentJob(AWSJob awsJob) {
        try {
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
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 
     * @param jobDetail
     * @param trigger
     * @throws SchedulerException
     * @apiNote use 1 of schedule engines to schedule job 
     */
    @Override
    public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
        try {
            //get appropriate scheduler
            SchedulerEngine schedulerEngine = this.scheduleService.getAppropriateEngine();
            schedulerEngine.scheduleJob(jobDetail, trigger);
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 
     * @param schedulerEngine
     * @param listener
     * @param matcher
     * 
     * @apiNote Add trigger listener into scheduler with particular matcher
     */
    @Override
    public void addTriggerListener(SchedulerEngine schedulerEngine, 
        TriggerListener listener, Matcher<TriggerKey> matcher) 
    {
        try {
            schedulerEngine.getListenerManager().addTriggerListener(listener, matcher);
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
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
    @Override
    public void addJobListener(SchedulerEngine schedulerEngine,
        JobListener listener, Matcher<JobKey> matcher)
    {
        try {
            schedulerEngine.getListenerManager().addJobListener(listener, matcher);
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

}
