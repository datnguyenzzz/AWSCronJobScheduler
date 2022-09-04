package com.github.datnguyenzzz.Components;

import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.JobExecuter;

@Component("awsSQSExecuter")
public class AWSSQSExecuter implements JobExecuter {

    private final Logger logger = LoggerFactory.getLogger(AWSSQSExecuter.class);

    @Override
    public void execute(JobDetail jobDetail) {
        // TODO Auto-generated method stub
        logger.info("Execute SQS job with name : " + jobDetail.getKey().toString());
    }
}
