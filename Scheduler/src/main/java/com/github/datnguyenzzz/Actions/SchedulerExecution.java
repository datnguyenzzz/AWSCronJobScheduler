package com.github.datnguyenzzz.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.impl.matchers.NotMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Components.QuartzScheduler;
import com.github.datnguyenzzz.Handlers.AddJobHandler;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component
public class SchedulerExecution {

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private QuartzScheduler scheduler;

    @Autowired
    private QuartzJobGenerator jobGenerator;

    @Autowired
    private AddJobHandler addJobHandler;

    @Autowired
    private JobHealthyStatusUpdateTriggerListener healthyTriggerListener;

    @Autowired
    private JobHealthyStatusUpdateJobListener healthyJobListener;

    private CronJobProvider provider;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    @PostConstruct
    private void init() {
        provider = ctx.getBean("cronJobProviderFactory", CronJobProvider.class);
        healthyTriggerListener.setName("Healthy update with trigger");
        healthyJobListener.setName("Healthy update with job");
    }

    /**
     * 
     * @param jobList
     * 
     * @implNote Add all job into scheduler without trigger implementation
     */
    private void prepareJob(JobListDefinition jobList) throws SchedulerException {
        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();

        for (String jobName: jobHashMap.keySet()) {
            // init store data map
            AWSJob awsJob = jobHashMap.get(jobName);
            //JobDetail awsJobDetail = jobGenerator.genPublishingJobDetail(awsJob);
            //this.scheduler.addJob(awsJobDetail, false);
            this.addJobHandler.addNewJob(awsJob);
        }
    }

    /**
     * 
     * @param jobList
     * @implNote add job trigger after its parent was executed
     */
    private void addAfterJobExecutedListener(JobListDefinition jobList) throws SchedulerException {
        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        Map<String, List<String>> jobExecutionOrder = jobList.getJobExecutionOrder();

        for(String jobName: jobHashMap.keySet()) {
            AWSJob awsJob = jobHashMap.get(jobName);
            JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob);

            if (!jobExecutionOrder.containsKey(jobName)) continue;
            if (jobExecutionOrder.get(jobName).size() == 0) continue;

            SequentialExecutionJobListener executionJobListener = ctx.getBean(SequentialExecutionJobListener.class);

            executionJobListener.setScheduler(this.scheduler);
            executionJobListener.setName(awsJobKey.toString());

            // prepare list of next job
            for (String jobNextName : jobExecutionOrder.get(jobName)) {
                AWSJob awsJobNext = jobHashMap.get(jobNextName);
                JobKey awsJobNextKey = this.jobGenerator.genJobKey(awsJobNext);
                JobDetail awsJobDetail = this.scheduler.getJobDetail(awsJobNextKey);

                executionJobListener.addToJobExecuteNext(awsJobDetail);
            }

            //add listener to Job that match JobKey
            this.scheduler.getListenerManager().addJobListener(executionJobListener, KeyMatcher.keyEquals(awsJobKey));
        }
    }

    /**
     * 
     * @param jobList
     * @implNote Schedule all job stored in scheduler, a job without trigger will be trigger after some other job was executed
     * @implNote Update healthy statuses within trigger listener
     */
    private void scheduleJob(JobListDefinition jobList) throws SchedulerException {
        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        for (String jobName: jobHashMap.keySet()) {
            AWSJob awsJob = jobHashMap.get(jobName);
            
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

    /**
     * @implNote Attach listener to trigger and job, except Health Cheack job, to update healthy status
     * @throws SchedulerException
     */
    private void updateHealthyStatus() throws SchedulerException {
        this.scheduler.getListenerManager().addTriggerListener(healthyTriggerListener, NotMatcher.not(GroupMatcher.triggerGroupEquals(HEALTH_CHECK_GROUP)));
        this.scheduler.getListenerManager().addJobListener(healthyJobListener, NotMatcher.not(GroupMatcher.jobGroupEquals(HEALTH_CHECK_GROUP)));
    }

    /**
     * 
     * @param jobList
     * @apiNote Immutable API
     */
    private void updateRelation(JobListDefinition jobList) {

        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        Map<String, List<String>> jobExecutionOrder = jobList.getJobExecutionOrder();

        for (AWSJob job: jobList.getJobList()) {
            String jobName = job.getName();
            jobHashMap.put(jobName, job);
        }

        for (AWSJob job: jobList.getJobList()) {
            if (job.getAfterJobDone()==null) continue;
            String jobName = job.getName();
            String jobBefore = job.getAfterJobDone();

            if (!jobExecutionOrder.containsKey(jobBefore))
                jobExecutionOrder.put(jobBefore, new ArrayList<>());

            jobExecutionOrder.get(jobBefore).add(jobName);
        }
    }

    public void start() throws SchedulerException {
        logger.info("Start scheduler execution ...");

        JobListDefinition jobList = provider.getDefinition();
        updateRelation(jobList);

        // execute job list by BFS order
        prepareJob(jobList);
        addAfterJobExecutedListener(jobList);

        updateHealthyStatus();

        //loggin all job
        for(String group: this.scheduler.getJobGroupNames()) {
            for(JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.groupEquals(group))) {
                logger.info("Found job identified by: " + jobKey.toString());
            }
        }

        scheduleJob(jobList);

    }
}
