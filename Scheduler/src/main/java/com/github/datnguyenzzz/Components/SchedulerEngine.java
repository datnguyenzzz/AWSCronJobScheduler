package com.github.datnguyenzzz.Components;

import java.util.List;
import java.util.Random;
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
import com.github.datnguyenzzz.Factories.QuartzCronJobFactory;

@Component
@Scope("prototype")
public class SchedulerEngine extends Object {
    
    private final static int MAX_NAME_LENGTH = 30;

    private Scheduler scheduler;
    private String name;

    public static String genRandomName() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = MAX_NAME_LENGTH;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
            (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Autowired
    private QuartzCronJobFactory cronJobFactory;

    @PostConstruct
    public void init() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();

            //need job factory, in order to instance job bean
            //because I pass job as bean
            this.scheduler.setJobFactory(this.cronJobFactory);
            
            this.scheduler.start();
            
            // get random name for the engine;
            StringBuilder sb = new StringBuilder();
            sb.append("engine-");

            String randomName = SchedulerEngine.genRandomName();
            sb.append(randomName);
            this.name = sb.toString();

        } catch (Exception ex) {
            throw new SystemException(ex.getMessage());
        }
    }

    public String getName() {
        return this.name;
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
