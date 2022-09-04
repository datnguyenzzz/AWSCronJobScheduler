package com.github.datnguyenzzz.Components;

import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.JobExecuter;

@Component("awsKinesisExecuter")
public class AWSKinesisExecuter implements JobExecuter {

    private final Logger logger = LoggerFactory.getLogger(AWSKinesisExecuter.class);

    @Override
    public void execute(JobDetail jobDetail) {
        // TODO Auto-generated method stub
        logger.info("Execute Kinesis job with name : " + jobDetail.getKey().toString());
    }
    
}
