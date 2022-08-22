package com.github.datnguyenzzz.Actions;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.QuartzScheduler;

/**
 * @apiNote Update job's healthy status
 */
@Component
public class JobHealthyStatusUpdateListener implements TriggerListener {

    @Value("${verbal.jobFired}")
    private String JOB_FIRED;

    @Value("${verbal.jobMisFired}")
    private String JOB_MISFIRED;

    @Value("${verbal.jobStatus}")
    private String JOB_STATUS;

    @Value("${verbal.isFinished}")
    private String IS_FINISHED;

    private String name;

    private QuartzScheduler scheduler;

    public void setScheduler(QuartzScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobCtx, CompletedExecutionInstruction arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobCtx) {
        // TODO Auto-generated method stub

        JobDataMap dataMap = jobCtx.getJobDetail().getJobDataMap();
        int oldFired = dataMap.getInt(JOB_FIRED);
        dataMap.put(JOB_FIRED, ++oldFired);
        
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // TODO Auto-generated method stub
        JobKey jobInvolved = trigger.getJobKey();
    }

    @Override
    public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
        //Accept all kind of jobs
        return false;
    }
    
}
