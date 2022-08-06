package com.github.datnguyenzzz.Components;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Jobs.PublishingJob;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.Message;

@Component
public class QuartzJobGenerator {

    private final static String JOB_TRIGGER = "jobTrigger";
    private final static String ACTION_FILE = "lambdaActionFile";
    private final static String PUBLISH_JOB_GROUP = "Job-publish-group";
    private final static String PUBLISH_TRIGGER_GROUP = "Trigger-publish-group";

    public QuartzJobGenerator() {}
    
    /**
     * 
     * @param awsJob
     * @return JobDeTail of awsJob
     */
    public JobDetail genJobDetail(AWSJob awsJob) {
        //TODO: Pack Trigger into jobDetail
        Trigger trigger = null;
        
        if (awsJob.getCronTrigger() != null) {
            trigger = TriggerBuilder.newTrigger()
                        .withIdentity(awsJob.getName(), PUBLISH_TRIGGER_GROUP)
                        .forJob(genJobKey(awsJob, PUBLISH_JOB_GROUP))
                        .withSchedule(
                            CronScheduleBuilder.cronSchedule(awsJob.getCronTrigger())
                        )      
                        .build();
        }
        
        //Get job data KV store by name from global store
        Map<String, Object> hMap = new HashMap<>();
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
                                        .withIdentity(genJobKey(awsJob, PUBLISH_JOB_GROUP))
                                        .storeDurably(isNeededDurable)
                                        .usingJobData(new JobDataMap(hMap))
                                        .build();

        return awsJobDetail;
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
}
