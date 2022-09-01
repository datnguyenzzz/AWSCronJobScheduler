package com.github.datnguyenzzz.Services;

import java.time.LocalDateTime;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Entities.AWSJob;
import com.github.datnguyenzzz.Entities.HealthStatus;
import com.github.datnguyenzzz.Entities.Message;
import com.github.datnguyenzzz.Jobs.HealthCheckJob;
import com.github.datnguyenzzz.Jobs.PublishingJob;

/**
 * @implNote Job generator is singleton
 * @apiNote Handle all relate to job creation
 */
@Component
@Scope("singleton")
public class QuartzJobGeneratorService {

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

    @Value("${verbal.jobFailed}")
    private String JOB_FAILED;

    @Value("${verbal.jobStatus}")
    private String JOB_STATUS;

    @Value("${verbal.isFinished}")
    private String IS_FINISHED;

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    @Value("${verbal.jobName}")
    private String JOB_NAME;

    public QuartzJobGeneratorService() {}

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
        hMap.put(JOB_FAILED, 0);
        hMap.put(JOB_STATUS, IS_FINISHED);
        return hMap;
    }

    /**
     * 
     * @param jobDataMap
     * @param name
     * @implNote muttable
     */
    public void setHSJobName(JobDataMap jobDataMap, String name) {
        jobDataMap.put(JOB_NAME, name);
    }

    /**
     * 
     * @param JobDataMap
     * @return Entity<HealthStatus>
     */
    public HealthStatus getHealthStatusFromDataMap(JobDataMap jobDataMap) {
        return new HealthStatus(LocalDateTime.now(), 
            jobDataMap.getString(JOB_NAME), 
            jobDataMap.getInt(JOB_FIRED), 
            jobDataMap.getInt(JOB_MISFIRED), 
            jobDataMap.getInt(JOB_COMPLETED),
            jobDataMap.getInt(JOB_FAILED), 
            jobDataMap.getString(JOB_STATUS));
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
     * @return Job that will trigger for Health CHeck job
     */
    public JobDetail genStatusHealthCheckJob(int timeWindow) {
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
        JobDetail jobDetail = JobBuilder.newJob(HealthCheckJob.class)
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
        //sb.append("-");
        //sb.append(job.getUsedService().toUpperCase());
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

    public JobKey genJobKey(String name) {
        return new JobKey(name, PUBLISH_JOB_GROUP);
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
