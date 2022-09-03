package com.github.datnguyenzzz.Listeners;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @apiNote Update job's healthy status
 */
@Component
@Scope("prototype")
public class JobHealthyStatusUpdateTriggerListener implements TriggerListener {

    @Value("${verbal.jobFired}")
    private String JOB_FIRED;

    @Value("${verbal.jobMisFired}")
    private String JOB_MISFIRED;

    @Value("${verbal.jobStatus}")
    private String JOB_STATUS;

    @Value("${verbal.isFinished}")
    private String IS_FINISHED;

    @Value("${verbal.isRunning}")
    private String IS_RUNNING;

    private String name;

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
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // will handle later on
    }

    @Override
    public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
        //Accept all kind of jobs
        return false;
    }
    
}
