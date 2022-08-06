package com.github.datnguyenzzz.Actions;

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

import com.github.datnguyenzzz.Components.QuartzScheduler;

public class SequentialExecutionJobListener implements JobListener {

    private final static String PUBLISH_TRIGGER_GROUP = "Trigger-publish-group";

    private final static Logger logger = LoggerFactory.getLogger(SequentialExecutionJobListener.class);

    private String name;
    private List<JobDetail> jobExecuteNext;

    private QuartzScheduler scheduler;

    public SequentialExecutionJobListener(QuartzScheduler scheduler) {
        this.jobExecuteNext = new ArrayList<>();
        this.scheduler = scheduler;
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
                this.scheduler.scheduleJob(trigger);
            }
            catch (SchedulerException ex) {
                logger.info("Can not execute " + jobDetail.getKey().toString());
            }
        }
    }
    
}
