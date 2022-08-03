package com.github.datnguyenzzz.Actions;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
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

    private void bfs(JobListDefinition jobList) throws SchedulerException {

        Map<String, AWSJob> jobHashMap = jobList.getJobHashMap();
        Map<String, List<String>> jobExecutionOrder = jobList.getJobExecutionOrder();

        Deque<String> dq = new LinkedList<>();

        Set<String> visited = new HashSet<>();

        for (String jobName: jobHashMap.keySet()) {
            // init store data map
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
            JobDetail awsJobDetail = jobGenerator.genJobDetail(awsJobNow);

            //Init Job Listener
            SequentialExecutionJobListener listener = 
                new SequentialExecutionJobListener(jobGenerator.genJobKey(awsJobNow, PUBLISH_JOB_GROUP).toString());

            if (jobExecutionOrder.containsKey(jobNow)) {

                for (String jobNext : jobExecutionOrder.get(jobNow)) {

                    if (visited.contains(jobNext)) continue;

                    //TODO: Set up trigger JobNext after JobNow
                    //TODO: By setting Job listener of jobNow after fired
                    //jobNext must execute after jobNow
                    AWSJob awsJobNext = jobHashMap.get(jobNext);
                    listener.addToAfterFiredJob(jobGenerator.genJobKey(awsJobNext, PUBLISH_JOB_GROUP));

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
