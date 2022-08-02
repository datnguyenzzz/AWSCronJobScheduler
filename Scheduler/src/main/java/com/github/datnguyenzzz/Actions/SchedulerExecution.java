package com.github.datnguyenzzz.Actions;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Exceptions.SystemException;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component
public class SchedulerExecution {

    @Autowired
    private ApplicationContext ctx;

    private Scheduler scheduler;
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    @PostConstruct
    private void init() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            //TODO: need job factory, in order to instance job bean
            
            this.scheduler.start();
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage());
        }
    }

    private JobDetail genJobDetail(AWSJob awsJob) {
        return null;
    }

    private void bfs(JobListDefinition jobList) {
        Deque<String> dq = new LinkedList<>();

        Set<String> visited = new HashSet<>();

        for (String jobName: jobList.getJobHashMap().keySet())
            visited.add(jobName);

        for (String jobName: jobList.getJobHashMap().keySet()) {
            if (!jobList.getJobExecutionOrder().containsKey(jobName)) continue;
            for (String jobNext : jobList.getJobExecutionOrder().get(jobName)) 
                visited.remove(jobNext);
        }

        //remain in visited is root
        for (String root: visited) dq.add(root);

        while (dq.size() > 0) {
            String jobNow = dq.pollFirst();

            //loggin
            logger.info("Triggered job: \n");
            logger.info(jobList.getJobHashMap().get(jobNow).toString());
            
            //TODO: Add jobNow to scheduler
            JobDetail awsJobDetail = genJobDetail(jobList.getJobHashMap().get(jobNow));
            
            //

            if (!jobList.getJobExecutionOrder().containsKey(jobNow)) continue;

            for (String jobNext : jobList.getJobExecutionOrder().get(jobNow)) {

                if (visited.contains(jobNext)) continue;

                //TODO: Set up trigger JobNext after JobNow
                //TODO: By setting Job/Trigger listener

                visited.add(jobNext);
                dq.add(jobNext);
            }
        }
    }

    public void start() {
        logger.info("Start scheduler execution ...");

        CronJobProvider provider = ctx.getBean("cronJobProviderFactory", CronJobProvider.class);
        JobListDefinition jobList = provider.getDefinition();
        jobList.updateRelation();

        // execute job list by BFS order
        bfs(jobList);
    }
}
