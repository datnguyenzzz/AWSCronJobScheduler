package com.github.datnguyenzzz.Actions;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Components.QuartzScheduler;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component
public class SchedulerExecution {

    private final static String JOB_TRIGGER = "jobTrigger";
    private final static String PUBLISH_JOB_GROUP = "Job-publish-group";

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private QuartzScheduler scheduler;

    @Autowired
    private QuartzJobGenerator jobGenerator;

    private CronJobProvider provider;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    @PostConstruct
    private void init() {
        provider = ctx.getBean("cronJobProviderFactory", CronJobProvider.class);
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
            JobDetail awsJobDetail = jobGenerator.genJobDetail(awsJob);
            this.scheduler.addJob(awsJobDetail, false);
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
            JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob, PUBLISH_JOB_GROUP);

            if (!jobExecutionOrder.containsKey(jobName)) continue;
            if (jobExecutionOrder.get(jobName).size() == 0) continue;

            SequentialExecutionJobListener listener = new SequentialExecutionJobListener();
            listener.setName(awsJobKey.toString());

            // prepare list of next job
            for (String jobNextName : jobExecutionOrder.get(jobName)) {
                AWSJob awsJobNext = jobHashMap.get(jobNextName);
                JobKey awsJobNextKey = this.jobGenerator.genJobKey(awsJobNext, PUBLISH_JOB_GROUP);
                JobDetail awsJobDetail = this.scheduler.getJobDetail(awsJobNextKey);

                listener.addToJobExecuteNext(awsJobDetail);
            }

            //add listener to Job that match JobKey
            this.scheduler.getListenerManager().addJobListener(listener, KeyMatcher.keyEquals(awsJobKey));
        }
    }

    /**
     * 
     * @param jobList
     * @implNote Schedule all job stored in scheduler, a job without trigger will be trigger after some other job was executed
     */
    private void scheduleJob(JobListDefinition jobList) throws SchedulerException {
        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        for (String jobName: jobHashMap.keySet()) {
            AWSJob awsJob = jobHashMap.get(jobName);
            
            // find corresponding job detail stored in scheduler
            JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob, PUBLISH_JOB_GROUP);
            JobDetail awsJobDetail = this.scheduler.getJobDetail(awsJobKey);

            JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
            Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

            //Add jobNow to scheduler
            if (trigger != null) 
                this.scheduler.scheduleJob(trigger);
        }
    }

    public void start() throws SchedulerException {
        logger.info("Start scheduler execution ...");

        JobListDefinition jobList = provider.getDefinition();
        jobList.updateRelation();

        // execute job list by BFS order
        prepareJob(jobList);
        addAfterJobExecutedListener(jobList);
        scheduleJob(jobList);

        //loggin all job
        //for(String group: this.scheduler.getJobGroupNames()) {
        //    for(JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.groupEquals(group))) {
        //        logger.info("Found job identified by: " + jobKey.toString());
        //    }
        //}
    }
}
