package com.github.datnguyenzzz.Actions;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;

public class SequentialExecutionJobListener implements JobListener {

    private String name;
    private List<JobKey> afterFiredJob;

    public SequentialExecutionJobListener (String name) {
        this.name = name;
        this.afterFiredJob = new ArrayList<>();
    }

    public void addToAfterFiredJob(JobKey jobKey) {
        this.afterFiredJob.add(jobKey);
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
    public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
        // TODO Auto-generated method stub
        
    }
    
}
