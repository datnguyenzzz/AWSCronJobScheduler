package com.github.datnguyenzzz.Actions;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialExecutionJobListener implements JobListener {

    private final static Logger logger = LoggerFactory.getLogger(SequentialExecutionJobListener.class);

    private String name;
    private List<JobKey> jobExecuteNext;

    public SequentialExecutionJobListener() {
        this.jobExecuteNext = new ArrayList<>();
    }

    /**
     * 
     * @param name = JobKey
     * @param jobExecuteNext = List<JobKey> must be execute next
     */
    public SequentialExecutionJobListener(String name) {
        this();
        this.name = name;
    }

    public void addToJobExecuteNext(JobKey jobKey) {
        this.jobExecuteNext.add(jobKey);
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
        JobDetail awsJobDetail = context.getJobDetail();
        logger.info("After job" + awsJobDetail.getKey().toString() + "done !!!");
        logger.info("Try to execute:");
        for (JobKey jobKey : this.jobExecuteNext) 
            logger.info("\t" + jobKey.toString());
    }
    
}
