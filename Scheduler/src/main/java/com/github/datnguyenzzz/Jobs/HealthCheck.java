package com.github.datnguyenzzz.Jobs;

import java.time.LocalDateTime;

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
import com.github.datnguyenzzz.Services.HealthCheckService;

@Component
@PersistJobDataAfterExecution
public class HealthCheck implements Job {

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

    @Value("${verbal.schedulerKey}")
    private String SCHEDULER_KEY;

    @Value("${verbal.healthCheckGroup}")
    private String HEALTH_CHECK_GROUP;

    @Autowired
    private HealthCheckService healthCheckHandler;

    private final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

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
                    logger.info("\t Fired: " + jobDataMap.getInt(JOB_FIRED));
                    logger.info("\t Misfired: " + jobDataMap.getInt(JOB_MISFIRED));
                    logger.info("\t Completed: " + jobDataMap.getInt(JOB_COMPLETED));
                    logger.info("\t Status: " + jobDataMap.getString(JOB_STATUS));

                    // add into health check handler
                    this.healthCheckHandler.addToHashMap(count, LocalDateTime.now(), 
                                                        jobKey.toString(), 
                                                        jobDataMap.getInt(JOB_FIRED), 
                                                        jobDataMap.getInt(JOB_MISFIRED), 
                                                        jobDataMap.getInt(JOB_COMPLETED), 
                                                        jobDataMap.getString(JOB_STATUS));
                }
            }
        }
        catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }
    
}
