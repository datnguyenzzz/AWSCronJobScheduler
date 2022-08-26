package com.github.datnguyenzzz.Components;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Exceptions.SystemException;
import com.github.datnguyenzzz.Factories.PublishingJobFactory;

/**
 * @implNote Make sure Quartz Scheduler is singleton
 */
@Component("quartzScheduler")
@Scope("singleton")
public class QuartzScheduler {
    
    private Scheduler scheduler;

    @Autowired
    private PublishingJobFactory publishingJobFactory;

    @PostConstruct
    public void init() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();

            //need job factory, in order to instance job bean
            //because I pass job as bean
            this.scheduler.setJobFactory(this.publishingJobFactory);
            
            this.scheduler.start();
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage());
        }
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        this.scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleJob(Trigger trigger) throws SchedulerException {
        this.scheduler.scheduleJob(trigger);
    }

    public void addJob(JobDetail jobDetail, boolean isReplace) throws SchedulerException {
        this.scheduler.addJob(jobDetail, isReplace);
    }

    public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
        return this.scheduler.getJobDetail(jobKey);
    }

    public ListenerManager getListenerManager() throws SchedulerException {
        return this.scheduler.getListenerManager();
    }

    public List<String> getJobGroupNames() throws SchedulerException {
        return this.scheduler.getJobGroupNames();
    }

    public Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher) throws SchedulerException {
        return this.scheduler.getJobKeys(matcher);
    }
}
