package com.github.datnguyenzzz.Listeners;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Services.QuartzJobGeneratorService;

@Component
public class JobHealthyStatusUpdateJobListener implements JobListener {

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

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
        //update job fired number
        this.quartzJobGeneratorService.addHSJobFired(dataMap);
        //update job current status
        this.quartzJobGeneratorService.setHSJobStatusIsRunning(dataMap);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobCtx, JobExecutionException arg1) {
        //nothing
        JobDataMap dataMap = jobCtx.getJobDetail().getJobDataMap();
        this.quartzJobGeneratorService.setHSJobStatusIsFinnished(dataMap);
    }
    
}
