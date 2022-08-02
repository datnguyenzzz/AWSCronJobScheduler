package com.github.datnguyenzzz.Components;

import javax.annotation.PostConstruct;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Factories.AWSPublisherFactory;
import com.github.datnguyenzzz.Interfaces.AWSPublisher;

@Component
@Scope("prototype")
public class PublishingJob implements Job {

    @Autowired
    private ApplicationContext ctx;

    private AWSPublisherFactory awsPublisherFactory;

    @PostConstruct
    public void init() {
        this.awsPublisherFactory = ctx.getBean("awsPublisherFactory", AWSPublisherFactory.class);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //get appropiate publisher
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        String usedService = jobDetail.getDescription();

        String jobName = jobDetail.getKey().toString();

        AWSPublisher publisher = this.awsPublisherFactory.getObject(usedService);
        publisher.publish(jobName);
    }
    
}
