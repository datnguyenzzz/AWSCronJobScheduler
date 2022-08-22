package com.github.datnguyenzzz.Actions;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JobHealthyStatusUpdateJobListener implements JobListener {

    @Value("${verbal.jobFired}")
    private String JOB_FIRED;

    @Value("${verbal.jobMisFired}")
    private String JOB_MISFIRED;

    @Value("${verbal.jobStatus}")
    private String JOB_STATUS;

    @Value("${verbal.isFinished}")
    private String IS_FINISHED;

    @Value("${verbal.jobCompleted}")
    private String JOB_COMPLETED;

    @Value("${verbal.isRunning}")
    private String IS_RUNNING;

    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobCtx) {
        JobDataMap dataMap = jobCtx.getJobDetail().getJobDataMap();
        int oldFired = dataMap.getInt(JOB_FIRED);
        //update job fired number
        dataMap.put(JOB_FIRED, ++oldFired);
        //update job current status
        dataMap.put(JOB_STATUS, IS_RUNNING);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobCtx, JobExecutionException arg1) {

        JobDataMap dataMap = jobCtx.getJobDetail().getJobDataMap();
        int oldCompleted = dataMap.getInt(JOB_COMPLETED);
        //update job fired number
        dataMap.put(JOB_COMPLETED, ++oldCompleted);
        //update job current status
        dataMap.put(JOB_STATUS, IS_FINISHED);
        
    }
    
}
