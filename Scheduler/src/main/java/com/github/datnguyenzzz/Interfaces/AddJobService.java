package com.github.datnguyenzzz.Interfaces;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.AWSJob;

public interface AddJobService {
    void addNewJob(AWSJob awsJob);
    void addSequentialTrigger(AWSJob jobFirst, List<AWSJob> listJobNext);
    void addSequentialTrigger(String jobFirst, AWSJob jobNext);
    void scheduleCurrentJob(AWSJob job);
    void scheduleJob(JobDetail jobDetail, Trigger trigger);
    void addTriggerListener(SchedulerEngine schedulerEngine, TriggerListener listener, Matcher<TriggerKey> matcher);
    void addJobListener(SchedulerEngine schedulerEngine, JobListener listener, Matcher<JobKey> matcher);
}
