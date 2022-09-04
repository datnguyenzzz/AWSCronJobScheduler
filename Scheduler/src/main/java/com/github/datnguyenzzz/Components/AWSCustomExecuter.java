package com.github.datnguyenzzz.Components;

import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.JobExecuter;

@Component("awsCustomExecuter")
public class AWSCustomExecuter implements JobExecuter {

    private final Logger logger = LoggerFactory.getLogger(AWSCustomExecuter.class);
    @Override
    public void execute(JobDetail jobDetail) {
        // TODO Auto-generated method stub
        logger.info("Execute custom job with name : " + jobDetail.getKey().toString());
    }

}
