package com.github.datnguyenzzz.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Services.SchedulerEngineDistributionHandlerService;

/**
 * @apiNote Trigger job sequentially
 */
@Component
@Scope("prototype")
public class SequentialExecutionJobListener implements JobListener {

    @Value("${verbal.triggerPublishGroup}")
    private String PUBLISH_TRIGGER_GROUP;

    @Autowired
    private SchedulerEngineDistributionHandlerService schedulerEngineService;

    private final static Logger logger = LoggerFactory.getLogger(SequentialExecutionJobListener.class);

    private String name;
    private List<JobDetail> jobExecuteNext;

    //private SchedulerEngine scheduler;

    public SequentialExecutionJobListener() {
        this.jobExecuteNext = new ArrayList<>();
    }

    public void addToJobExecuteNext(JobDetail jobDetail) {
        this.jobExecuteNext.add(jobDetail);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext arg0) {
        // Do nothing
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext arg0) {
        // Do nothing
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {
        // TODO Auto-generated method stub
        //JobDetail awsJobDetail = context.getJobDetail();
        //logger.info("After job" + awsJobDetail.getKey().toString() + "done !!!");
        //logger.info("Try to execute:");
        for (JobDetail jobDetail : this.jobExecuteNext) {
            //logger.info("\t" + jobDetail.getKey().toString());
            //1-shot trigger

            Trigger trigger = TriggerBuilder.newTrigger()
                                .withIdentity(jobDetail.getKey().toString(), PUBLISH_TRIGGER_GROUP)
                                .forJob(jobDetail)
                                .build();
            try {
                // get engine where job reside
                SchedulerEngine engine = this.schedulerEngineService.getSchedulerEngineByJobKey(jobDetail.getKey());
                engine.scheduleJob(trigger);
            }
            catch (SchedulerException ex) {
                logger.info("Can not execute " + jobDetail.getKey().toString());
            }
        }
    }
    
}
