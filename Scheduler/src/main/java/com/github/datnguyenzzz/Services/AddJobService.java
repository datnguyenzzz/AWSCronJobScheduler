package com.github.datnguyenzzz.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Entities.AWSJob;

@Component
@Scope("prototype")
public class AddJobService {

    @Autowired
    private SchedulerEngineDistributionHandlerService scheduleService;

    @Autowired
    private QuartzJobGeneratorService jobGenerator;

    /**
     * 
     * @param job
     * @apiNote Add new job to scheduler
     */
    public void addNewJob(AWSJob awsJob) throws SchedulerException {
        JobDetail jobDetail = this.jobGenerator.genPublishingJobDetail(awsJob);
        this.scheduleService.addJob(jobDetail, false);
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobsExecuteNext
     * @apiNote Add trigger to handle sequential execution of list of job
     */
    public void addSequentialTrigger(AWSJob jobExecuteFirst, List<AWSJob> listJobsExecuteNext) throws SchedulerException {
        
        JobKey awsJobKeyFirst = this.jobGenerator.genJobKey(jobExecuteFirst);
        List<JobKey> awsJobKeyListNext = listJobsExecuteNext.stream()
                                            .map((awsJob) -> this.jobGenerator.genJobKey(awsJob))
                                            .collect(Collectors.toList());

        this.scheduleService.addSequentialTrigger(awsJobKeyFirst, awsJobKeyListNext);
    }

    /**
     * 
     * @param jobExecuteFirst
     * @param jobExecuteNext
     * @apiNote handle job sequential trigger
     */
    public void addSequentialTrigger(String jobExecuteFirst, AWSJob jobExecuteNext) throws SchedulerException {
        JobKey awsJobKeyFirst = this.jobGenerator.genJobKey(jobExecuteFirst);
        JobKey awsJobKeyNext = this.jobGenerator.genJobKey(jobExecuteNext);

        this.scheduleService.addSequentialTrigger(awsJobKeyFirst, awsJobKeyNext);
    }

    /**
     * 
     * @param awsjob
     * @apiNote schedule all job which current reside in scheduler
     */
    public void scheduleCurrentJob(AWSJob awsJob) throws SchedulerException {
        // find corresponding job detail stored in scheduler
        JobKey awsJobKey = this.jobGenerator.genJobKey(awsJob);
        this.scheduleService.scheduleCurrentJob(awsJobKey);
    }

}
