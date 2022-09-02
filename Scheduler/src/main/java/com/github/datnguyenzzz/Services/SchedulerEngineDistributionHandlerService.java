package com.github.datnguyenzzz.Services;

import java.util.HashMap;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Actions.SequentialExecutionJobListener;
import com.github.datnguyenzzz.Components.SchedulerEngine;

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

    private HashMap<JobKey, SchedulerEngine> jobRepository = new HashMap<>();

    /**
     * @apiNote Return appropriate schedule engine to handle the job 
     * */ 
    private SchedulerEngine getAppropriateEngine() {
        //TODO: current just use a scheduler as singleton

        return this.schedulerEngine;
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
        SchedulerEngine targetEngine = this.jobRepository.get(jobExecuteFirst);
        executionJobListener.setScheduler(targetEngine);
        executionJobListener.setName(jobExecuteFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        SchedulerEngine jobNextSchedulerEngine = this.jobRepository.get(jobExecuteNext);
        JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(jobExecuteNext);

        executionJobListener.addToJobExecuteNext(awsJobDetailNext);

        //add listener to target Engine

        targetEngine.getListenerManager().addJobListener(executionJobListener, 
                                                    KeyMatcher.keyEquals(jobExecuteFirst));
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
        SchedulerEngine targetEngine = this.jobRepository.get(jobExecuteFirst);
        executionJobListener.setScheduler(targetEngine);
        executionJobListener.setName(jobExecuteFirst.toString() + " bonus");

        //get engine where jobExecuteNext reside
        for (JobKey jobExecuteNext: jobListExecuteNext) {
            SchedulerEngine jobNextSchedulerEngine = this.jobRepository.get(jobExecuteNext);
            JobDetail awsJobDetailNext = jobNextSchedulerEngine.getJobDetail(jobExecuteNext);
            executionJobListener.addToJobExecuteNext(awsJobDetailNext);
        }

        //add listener to target Engine
        targetEngine.getListenerManager().addJobListener(executionJobListener, 
                                                    KeyMatcher.keyEquals(jobExecuteFirst));
    }

    /**
     * 
     * @param jobKey
     * @apiNote schedule job within schedule engine
     */
    public void scheduleCurrentJob(JobKey jobKey) throws SchedulerException {
        //get engine where jobKey reside
        SchedulerEngine schedulerEngine = this.jobRepository.get(jobKey);
        JobDetail awsJobDetail = schedulerEngine.getJobDetail(jobKey);

        JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
        Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

        //Add jobNow to scheduler
        if (trigger != null) 
            schedulerEngine.scheduleJob(trigger);
    }

}
