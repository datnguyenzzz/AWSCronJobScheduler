package com.github.datnguyenzzz.Services;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.datnguyenzzz.Components.SchedulerEngine;
import com.github.datnguyenzzz.Entities.HealthStatus;

/**
 * Equally distribute job across Scheduler engines
 */
@Service
@Scope("singleton")
@SuppressWarnings("unused")
public class SchedulerEngineDistributionHandlerService {

    @Autowired
    private SchedulerEngine schedulerEngine;

    @Autowired
    private HealthCheckService healthCheckService;

    @Autowired
    private QuartzJobGeneratorService quartzJobGeneratorService;

    private final Logger logger = LoggerFactory.getLogger(SchedulerEngineDistributionHandlerService.class);

    //TODO: Better using database
    private HashMap<JobKey, SchedulerEngine> jobRepository = new HashMap<>();

    /**
     * @apiNote Return appropriate schedule engine to handle the job 
     * */ 
    public SchedulerEngine getAppropriateEngine() {
        //TODO: current just use a scheduler as singleton

        return this.schedulerEngine;
    }

    /**
     * @apiNote Retrieve all schedule engine, where at least contains 1 job
     * @implNote Better using metadata DB
     * @return
     */
    public List<SchedulerEngine> getAllSchedulerEngines() {
        List<SchedulerEngine> res = jobRepository.entrySet().stream()
                                        .map(x -> x.getValue())
                                        .filter((engine) -> {
                                            int jobCount = 0;

                                            try {
                                                for (String group : engine.getJobGroupNames())
                                                    for (JobKey key: engine.getJobKeys(GroupMatcher.groupEquals(group))) {
                                                        //logger.info(key.toString());
                                                        jobCount++;
                                                    }
                                            }
                                            catch (Exception ex) {
                                                return false;
                                            }

                                            return (jobCount>0);
                                        })
                                        .collect(Collectors.toList());

        return res;
    }

    /**
     * 
     * @param jobKey
     * @return Scheduler Engine
     * @apiNote return scheduler engine where job key reside
     */
    public SchedulerEngine getSchedulerEngineByJobKey(JobKey jobKey) {
        return this.jobRepository.get(jobKey);
    }

    public void addJobKeyIntoRepo(JobKey jobKey, SchedulerEngine engine) {
        this.jobRepository.put(jobKey, engine);
    }

    /**
     * @apiNote Aggregate all health status of job which store inside repository
     */
    public void aggregateJobsHealthStatus() throws SchedulerException {

        int count = 0;
        for (JobKey jobKey : this.jobRepository.keySet()) {
            count++;
            logger.info("JOB #" + count + ": ");
            logger.info("\t Name : " + jobKey.toString());

            //find engine where jobKey reside
            SchedulerEngine schedulerEngine = this.getSchedulerEngineByJobKey(jobKey);
            JobDataMap jobDataMap = schedulerEngine.getJobDetail(jobKey).getJobDataMap();

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
