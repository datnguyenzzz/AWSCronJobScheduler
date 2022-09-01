package com.github.datnguyenzzz.Jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.QuartzScheduler;
import com.github.datnguyenzzz.Entities.HealthStatus;
import com.github.datnguyenzzz.Services.HealthCheckService;
import com.github.datnguyenzzz.Services.QuartzJobGeneratorService;

@Component
@PersistJobDataAfterExecution
public class HealthCheckJob implements Job {
    
    @Value("${verbal.schedulerKey}")
    private String SCHEDULER_KEY;

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

    private final Logger logger = LoggerFactory.getLogger(HealthCheckJob.class);

    @Override
    public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
        // TODO Auto-generated method stub
        logger.info("HEALTH CHECKING !!!!!");

        JobDataMap dataMap = jobCtx.getJobDetail().getJobDataMap();
        QuartzScheduler scheduler = (QuartzScheduler) dataMap.get(SCHEDULER_KEY);

        try {
            int count=0;
            for(String group: scheduler.getJobGroupNames()) {
                if (group.equals(HEALTH_CHECK_GROUP)) continue;
                for(JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(group))) {
                    count++;
                    logger.info("JOB #" + count + ": ");
                    logger.info("\t Name : " + jobKey.toString());

                    JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
                    this.quartzJobGeneratorService.setHSJobName(jobDataMap, jobKey.toString());
                    HealthStatus healthStatus = this.quartzJobGeneratorService.getHealthStatusFromDataMap(jobDataMap);

                    logger.info("\t Fired: " + healthStatus.getJobFired());
                    logger.info("\t Misfired: " + healthStatus.getJobMisfired());
                    logger.info("\t Completed: " + healthStatus.getJobCompleted());
                    logger.info("\t Failed: " + healthStatus.getJobFailed());
                    logger.info("\t Status: " + healthStatus.getJobStatus());

                    // add into health check handler
                    this.healthCheckService.addToHashMap(count, healthStatus);
                }
            }
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }
    
}
