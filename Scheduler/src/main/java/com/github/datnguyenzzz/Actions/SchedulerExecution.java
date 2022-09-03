package com.github.datnguyenzzz.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Entities.AWSJob;
import com.github.datnguyenzzz.Entities.JobListDefinition;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.Services.AddJobServiceImpl;
import com.github.datnguyenzzz.Services.HealthCheckService;

@Component
public class SchedulerExecution {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private AddJobServiceImpl addJobHandler;

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

            if (!jobExecutionOrder.containsKey(jobName)) continue;
            if (jobExecutionOrder.get(jobName).size() == 0) continue;

            //tranfrom List<Job_Name> -> List<Job_Object>
            List<AWSJob> listJobsNext = jobExecutionOrder.get(jobName).stream()
                                            .map(name -> jobHashMap.get(name))
                                            .collect(Collectors.toList());

            this.addJobHandler.addSequentialTrigger(awsJob, listJobsNext);
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
            this.addJobHandler.scheduleCurrentJob(awsJob);
        }
    }

    /**
     * @implNote Attach listener to trigger and job, except Health Cheack job, to update healthy status
     * @throws SchedulerException
     */
    private void updateHealthyStatus() throws SchedulerException {
        this.healthCheckService.updateListenerToUpdateHealthStatus();
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
        scheduleJob(jobList);

    }
}
