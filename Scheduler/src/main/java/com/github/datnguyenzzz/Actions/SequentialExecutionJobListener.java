package com.github.datnguyenzzz.Actions;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class SequentialExecutionJobListener implements JobListener {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
        // TODO Auto-generated method stub
        
    }
    
}
