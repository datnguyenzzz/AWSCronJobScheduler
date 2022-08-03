package com.github.datnguyenzzz.Actions;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.PublishingJob;
import com.github.datnguyenzzz.Components.QuartzScheduler;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.JobListDefinition;
import com.github.datnguyenzzz.dto.Message;

@Component
public class SchedulerExecution {

    private final static String JOB_TRIGGER = "jobTrigger";
    private final static String ACTION_FILE = "lambdaActionFile";
    private final static String PUBLISH_JOB_GROUP = "Job-publish-group";
    private final static String PUBLISH_TRIGGER_GROUP = "Trigger-publish-group";

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private QuartzScheduler scheduler;

    private CronJobProvider provider;
    private Map<String, Map<String, Object>> storeJobDataMap;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    @PostConstruct
    private void init() {
        storeJobDataMap = new HashMap<>();
        provider = ctx.getBean("cronJobProviderFactory", CronJobProvider.class);
    }

    private void bfs(JobListDefinition jobList) throws SchedulerException {

        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        Map<String, List<String>> jobExecutionOrder = jobList.getJobExecutionOrder();

        Deque<String> dq = new LinkedList<>();

        Set<String> visited = new HashSet<>();

        for (String jobName: jobHashMap.keySet()) {
            // init store data map
            this.storeJobDataMap.put(jobName, new HashMap<>());
            visited.add(jobName);
        }

        for (String jobName: jobHashMap.keySet()) {
            if (!jobExecutionOrder.containsKey(jobName)) continue;
            for (String jobNext : jobExecutionOrder.get(jobName)) 
                visited.remove(jobNext);
        }

        //remain in visited is root
        for (String root: visited) dq.add(root);

        while (dq.size() > 0) {
            String jobNow = dq.pollFirst();

            //loggin
            logger.info("Triggered job: \n");
            logger.info(jobHashMap.get(jobNow).toString());

            //Init Job Detail
            AWSJob awsJobNow = jobHashMap.get(jobNow);
            JobDetail awsJobDetail = genJobDetail(awsJobNow);

            if (jobExecutionOrder.containsKey(jobNow)) {

                for (String jobNext : jobExecutionOrder.get(jobNow)) {

                    if (visited.contains(jobNext)) continue;

                    //TODO: Set up trigger JobNext after JobNow
                    //TODO: By setting Job listener of jobNow after fired
                    //jobNext must execute after jobNow

                    visited.add(jobNext);
                    dq.add(jobNext);
                }
            }

            JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
            Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

            //Add jobNow to scheduler
            if (trigger != null) {
                this.scheduler.scheduleJob(awsJobDetail, trigger);
            } else {
                //this mean job is executed after another job fire
                //store job into scheduler for future run
                this.scheduler.addJob(awsJobDetail, false);
            }
        }
    }

    /**
     * 
     * @param awsJob
     * @return JobDeTail
     */
    private JobDetail genJobDetail(AWSJob awsJob) {
        //TODO: Pack Trigger into jobDetail
        Trigger trigger = null;
        
        if (awsJob.getCronTrigger() != null) {
            trigger = TriggerBuilder.newTrigger()
                        .withIdentity(awsJob.getName(), PUBLISH_TRIGGER_GROUP)
                        .withSchedule(
                            CronScheduleBuilder.cronSchedule(awsJob.getCronTrigger())
                        )      
                        .build();
        }
        
        //Get job data KV store by name from global store
        Map<String, Object> hMap = this.storeJobDataMap.get(awsJob.getName());
        hMap.put(JOB_TRIGGER, trigger);

        if (awsJob.getLambdaActionFile() != null) 
            hMap.put(ACTION_FILE, awsJob.getLambdaActionFile());

        if (awsJob.getMessages() != null) {
            for (Message mes: awsJob.getMessages()) 
                hMap.put(mes.getKey(), mes.getValue());
        }

        boolean isNeededDurable = (awsJob.getCronTrigger() != null 
                                || awsJob.getAfterJobDone() != null);

        //Pass usedService paramenter into Job description, 
        //in order to get corresponding Bean ....AWSPublisher
        JobDetail awsJobDetail = JobBuilder.newJob(PublishingJob.class)
                                        .withDescription(awsJob.getUsedService())
                                        .withIdentity(getJobKey(awsJob, PUBLISH_JOB_GROUP))
                                        .storeDurably(isNeededDurable)
                                        .usingJobData(new JobDataMap(hMap))
                                        .build();

        return awsJobDetail;
    }

    /**
     * 
     * @param job
     * @param group
     * @return JobKey
     */
    private JobKey getJobKey(AWSJob job, String group) {
        StringBuilder sb = new StringBuilder();
        sb.append(group);
        sb.append("-");
        sb.append(job.getUsedService().toUpperCase());
        return new JobKey(job.getName(), sb.toString());
    }

    public void start() throws SchedulerException {
        logger.info("Start scheduler execution ...");

        JobListDefinition jobList = provider.getDefinition();
        jobList.updateRelation();

        // execute job list by BFS order
        bfs(jobList);

        //loggin all job
        //for(String group: this.scheduler.getJobGroupNames()) {
        //    for(JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.groupEquals(group))) {
        //        logger.info("Found job identified by: " + jobKey.toString());
        //    }
        //}
    }
}
