package com.github.datnguyenzzz.Components;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Jobs.HealthCheck;
import com.github.datnguyenzzz.Jobs.PublishingJob;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.Message;

@Component
public class QuartzJobGenerator {

    @Value("${verbal.jobTrigger}")
    private String JOB_TRIGGER;

    @Value("${verbal.lambdaActionFile}")
    private String ACTION_FILE;

    @Value("${verbal.jobPublishGroup}")
    private String PUBLISH_JOB_GROUP;

    @Value("${verbal.triggerPublishGroup}")
    private String PUBLISH_TRIGGER_GROUP;

    @Value("${verbal.jobFired}")
    private String JOB_FIRED;

    @Value("${verbal.jobMisFired}")
    private String JOB_MISFIRED;

    @Value("${verbal.jobCompleted}")
    private String JOB_COMPLETED;

    @Value("${verbal.jobStatus}")
    private String JOB_STATUS;

    @Value("${verbal.isFinished}")
    private String IS_FINISHED;

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    public QuartzJobGenerator() {}

    /**
     * 
     * @apiNote Default data will be persisted within a job itself
     * 
     * @category job executioned number (fired) , current status 
     * , number of job is waiting(after misfired) 
     */
    public Map<String, Object> genDefaultDataMapContent() {
        Map<String, Object> hMap = new HashMap<>();
        hMap.put(JOB_FIRED,0);
        hMap.put(JOB_MISFIRED, 0);
        hMap.put(JOB_COMPLETED, 0);
        hMap.put(JOB_STATUS, IS_FINISHED);
        return hMap;
    }
    
    /**
     * 
     * @param awsJob
     * @return JobDeTail of awsJob
     */
    public JobDetail genPublishingJobDetail(AWSJob awsJob) {
        //TODO: Pack Trigger into jobDetail
        Trigger trigger = null;
        
        if (awsJob.getCronTrigger() != null) {
            trigger = TriggerBuilder.newTrigger()
                        .withIdentity(genTriggerKey(awsJob))
                        .forJob(genJobKey(awsJob))
                        .withSchedule(
                            CronScheduleBuilder.cronSchedule(awsJob.getCronTrigger())
                        )      
                        .build();
        }
        
        //Get job data KV store by name from global store
        Map<String, Object> hMap = genDefaultDataMapContent();
        hMap.put(JOB_TRIGGER, trigger);

        if (awsJob.getLambdaActionFile() != null) 
            hMap.put(ACTION_FILE, awsJob.getLambdaActionFile());

        if (awsJob.getMessages() != null) {
            for (Message mes: awsJob.getMessages()) 
                hMap.put(mes.getKey(), mes.getValue());
        }

        boolean isNeededDurable = (awsJob.getCronTrigger() != null 
                                || awsJob.getAfterJobDone() != null);

        //Pass usedService paramenter into Job description, 
        //in order to get corresponding Bean ....AWSPublisher
        JobDetail awsJobDetail = JobBuilder.newJob(PublishingJob.class)
                                        .withDescription(awsJob.getUsedService())
                                        .withIdentity(genJobKey(awsJob))
                                        .storeDurably(isNeededDurable)
                                        .usingJobData(new JobDataMap(hMap))
                                        .build();

        return awsJobDetail;
    }

    /**
     * 
     * @param timeWindow
     * @return Job that will trigger aggregate time series DB
     */
    public JobDetail genStatusAggregateJob(int timeWindow) {
        //TODO: generate job with trigger inside DataMap

        String name = "Healthy Status Check";

        JobKey jobKey = genJobKey(name, HEALTH_CHECK_GROUP);
        TriggerKey triggerKey = genTriggerKey(name, HEALTH_CHECK_GROUP);

        //job data map
        Map<String, Object> hMap = new HashMap<>();

        //gen trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(triggerKey)
                            .startNow()
                            .withSchedule(
                                SimpleScheduleBuilder.repeatSecondlyForever(timeWindow)
                            )
                            .build();

        hMap.put(JOB_TRIGGER, trigger);

        //gen job
        JobDetail jobDetail = JobBuilder.newJob(HealthCheck.class)
                                .withIdentity(jobKey)
                                .storeDurably(true)
                                .usingJobData(new JobDataMap(hMap))
                                .build();
        return jobDetail;
    }

    /**
     * 
     * @param job
     * @param group
     * @return JobKey = jobName + group + usedService
     */
    public JobKey genJobKey(AWSJob job, String group) {
        StringBuilder sb = new StringBuilder();
        sb.append(group);
        sb.append("-");
        sb.append(job.getUsedService().toUpperCase());
        return new JobKey(job.getName(), sb.toString());
    }

    /**
     * 
     * @param name
     * @param group
     * @return JobKey
     */
    public JobKey genJobKey(String name, String group) {
        return new JobKey(name, group);
    }

    public JobKey genJobKey(AWSJob job) {
        return genJobKey(job, PUBLISH_JOB_GROUP);
    }

    /**
     * 
     * @param name
     * @param group
     * @return trigger key
     */
    public TriggerKey genTriggerKey(AWSJob job, String group) {
        return new TriggerKey(job.getName(), group);
    }

    public TriggerKey genTriggerKey(String name, String group) {
        return new TriggerKey(name, group);
    }

    public TriggerKey genTriggerKey(AWSJob job) {
        return genTriggerKey(job, PUBLISH_TRIGGER_GROUP);
    }
}
