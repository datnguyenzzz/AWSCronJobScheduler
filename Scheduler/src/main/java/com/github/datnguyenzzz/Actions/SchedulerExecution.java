package com.github.datnguyenzzz.Actions;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.PublishingJob;
import com.github.datnguyenzzz.Exceptions.SystemException;
import com.github.datnguyenzzz.Factories.PublishingJobFactory;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.JobListDefinition;
import com.github.datnguyenzzz.dto.Message;

@Component
public class SchedulerExecution {

    private final static String JOB_TRIGGER = "jobTrigger";
    private final static String ACTION_FILE = "lambdaActionFile";

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private PublishingJobFactory publishingJobFactory;

    private Scheduler scheduler;
    private CronJobProvider provider;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    @PostConstruct
    private void init() {
        provider = ctx.getBean("cronJobProviderFactory", CronJobProvider.class);
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();

            //need job factory, in order to instance job bean
            //because I pass job as bean
            this.scheduler.setJobFactory(this.publishingJobFactory);
            
            this.scheduler.start();
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage());
        }
    }

    private void bfs(JobListDefinition jobList) throws SchedulerException {

        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        Map<String, List<String>> jobExecutionOrder = jobList.getJobExecutionOrder();

        Deque<String> dq = new LinkedList<>();

        Set<String> visited = new HashSet<>();

        for (String jobName: jobHashMap.keySet())
            visited.add(jobName);

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

            //TODO: Init Job Detail
            AWSJob awsJobNow = jobHashMap.get(jobNow);
            JobDetail awsJobDetail = genJobDetail(awsJobNow);

            if (jobExecutionOrder.containsKey(jobNow)) {

                for (String jobNext : jobExecutionOrder.get(jobNow)) {

                    if (visited.contains(jobNext)) continue;

                    //TODO: Set up trigger JobNext after JobNow
                    //TODO: By setting Job/Trigger listener

                    visited.add(jobNext);
                    dq.add(jobNext);
                }
            }

            JobDataMap jobDataMap = awsJobDetail.getJobDataMap();
            Trigger trigger = (Trigger) jobDataMap.get(JOB_TRIGGER);

            //Add jobNow to scheduler
            this.scheduler.scheduleJob(awsJobDetail, trigger);
        }
    }

    private JobDetail genJobDetail(AWSJob awsJob) {

        //TODO: Pack Trigger into jobDetail
        //Example trigger
        Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(awsJob.getName(), "Trigger-publish-group")
        .startNow()
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
                                 .withIntervalInSeconds(10)
                                 .repeatForever()
            )            
        .build();
        
        //TODO: Build job data KV store
        Map<String, Object> hMap = new HashMap<>();
        hMap.put(JOB_TRIGGER, trigger);

        if (awsJob.getLambdaActionFile() != null) 
            hMap.put(ACTION_FILE, awsJob.getLambdaActionFile());

        if (awsJob.getMessages() != null) {
            for (Message mes: awsJob.getMessages()) 
                hMap.put(mes.getKey(), mes.getValue());
        }

        //Pass usedService paramenter into Job description, 
        //in order to get corresponding Bean ....AWSPublisher
        JobDetail awsJobDetail = JobBuilder.newJob(PublishingJob.class)
                                        .withDescription(awsJob.getUsedService())
                                        .withIdentity(awsJob.getName(), "Job-publish-group")
                                        .usingJobData(new JobDataMap(hMap))
                                        .build();

        return awsJobDetail;
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
