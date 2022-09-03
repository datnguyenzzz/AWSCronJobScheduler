package com.github.datnguyenzzz.Services;

import java.util.HashMap;
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
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Actions.SequentialExecutionJobListener;
import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.HealthStatus;

/**
 * Equally distribute job across Scheduler engines
 */
@Service
public class SchedulerEngineDistributionHandlerService {

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Autowired
    private ApplicationContext ctx;
    
    @Autowired
    private SchedulerEngine schedulerEngine;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

    private final Logger logger = LoggerFactory.getLogger(SchedulerEngineDistributionHandlerService.class);

    //TODO: Better using database
    private HashMap<JobKey, SchedulerEngine> jobRepository = new HashMap<>();

    /**
     * @apiNote Return appropriate schedule engine to handle the job 
     * */ 
    private SchedulerEngine getAppropriateEngine() {
        //TODO: current just use a scheduler as singleton

        return this.schedulerEngine;
    }

    /**
     * @apiNote Retrieve all schedule engine, where at least contains 1 job
     * @implNote Better using metadata DB
     * @return
     */
    public List<SchedulerEngine> getAllSchedulerEngines() {
        List<SchedulerEngine> res = jobRepository.entrySet().stream()
                                        .map(x -> x.getValue())
                                        .filter((engine) -> {
                                            int jobCount = 0;

                                            try {
                                                for (String group : engine.getJobGroupNames())
                                                    for (JobKey key: engine.getJobKeys(GroupMatcher.groupEquals(group))) {
                                                        logger.info(key.toString());
                                                        jobCount++;
                                                    }
                                            }
                                            catch (Exception ex) {
                                                return false;
                                            }

                                            return (jobCount>0);
                                        })
                                        .collect(Collectors.toList());

        return res;
    }

    /**
     * 
     * @param jobKey
     * @return Scheduler Engine
     * @apiNote return scheduler engine where job key reside
     */
    private SchedulerEngine getSchedulerEngineByJobKey(JobKey jobKey) {
        return this.jobRepository.get(jobKey);
    }

    /**
     * 
     * @param jobDetail
     * @apiNote Add job into appropiate schedule
     */
    public void addJob(JobDetail jobDetail, boolean isReplace) throws SchedulerException {
        SchedulerEngine targetEngine = this.getAppropriateEngine();
        targetEngine.addJob(jobDetail, isReplace);

        // store into repository
        JobKey jobKey = jobDetail.getKey();
        jobRepository.put(jobKey, targetEngine);
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobExecuteNext
     * @throws SchedulerException
     * @apiNote Add trigger to execute jobExecuteNext after jobExecuteFirst
     */
    public void addSequentialTrigger(JobKey jobExecuteFirst, JobKey jobExecuteNext) throws SchedulerException {
        SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

        //get engine where jobExecuteFirst reside
        SchedulerEngine targetEngine = this.getSchedulerEngineByJobKey(jobExecuteFirst);
        executionJobListener.setScheduler(targetEngine);
        executionJobListener.setName(jobExecuteFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        SchedulerEngine jobNextSchedulerEngine = this.getSchedulerEngineByJobKey(jobExecuteNext);
        JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(jobExecuteNext);

        executionJobListener.addToJobExecuteNext(awsJobDetailNext);

        //add listener to target Engine
        this.addJobListener(targetEngine, executionJobListener, KeyMatcher.keyEquals(jobExecuteFirst));
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobListExecuteNext
     * @apiNote add trigger to execute List of jobExecuteNext after jobExecuteFirst
     */
    public void addSequentialTrigger(JobKey jobExecuteFirst, List<JobKey> jobListExecuteNext)
        throws SchedulerException 
    {
        SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

        //get engine where jobExecuteFirst reside
        SchedulerEngine targetEngine = this.getSchedulerEngineByJobKey(jobExecuteFirst);
        executionJobListener.setScheduler(targetEngine);
        executionJobListener.setName(jobExecuteFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        for (JobKey jobExecuteNext: jobListExecuteNext) {
            SchedulerEngine jobNextSchedulerEngine = this.getSchedulerEngineByJobKey(jobExecuteNext);
            JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(jobExecuteNext);
            executionJobListener.addToJobExecuteNext(awsJobDetailNext);
        }

        //add listener to target Engine
        this.addJobListener(targetEngine, executionJobListener, KeyMatcher.keyEquals(jobExecuteFirst));
    }

    /**
     * 
     * @param jobKey
     * @apiNote schedule job within schedule engine
     */
    public void scheduleCurrentJob(JobKey jobKey) throws SchedulerException {
        //get engine where jobKey reside
        SchedulerEngine schedulerEngine = this.getSchedulerEngineByJobKey(jobKey);
        JobDetail awsJobDetail = schedulerEngine.getJobDetail(jobKey);

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
        SchedulerEngine schedulerEngine = this.getAppropriateEngine();
        schedulerEngine.scheduleJob(jobDetail, trigger);
    }

    /**
     * @apiNote Aggregate all health status of job which store inside repository
     */
    public void aggregateJobsHealthStatus() throws SchedulerException {

        int count = 0;
        for (JobKey jobKey : this.jobRepository.keySet()) {
            count++;
            logger.info("JOB #" + count + ": ");
            logger.info("\t Name : " + jobKey.toString());

            //find engine where jobKey reside
            SchedulerEngine schedulerEngine = this.getSchedulerEngineByJobKey(jobKey);
            JobDataMap jobDataMap = schedulerEngine.getJobDetail(jobKey).getJobDataMap();

            this.quartzJobGeneratorService.setHSJobName(jobDataMap, jobKey.toString());
            HealthStatus healthStatus = this.quartzJobGeneratorService.getHealthStatusFromDataMap(jobDataMap);

            logger.info("\t Fired: " + healthStatus.getJobFired());
            logger.info("\t Misfired: " + healthStatus.getJobMisfired());
            logger.info("\t Completed: " + healthStatus.getJobCompleted());
            logger.info("\t Failed: " + healthStatus.getJobFailed());
            logger.info("\t Status: " + healthStatus.getJobStatus());

            // add into health check handler
            this.healthCheckService.addToHashMap(count, healthStatus);
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
