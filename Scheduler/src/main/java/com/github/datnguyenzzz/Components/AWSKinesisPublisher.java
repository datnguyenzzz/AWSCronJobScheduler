package com.github.datnguyenzzz.Components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.AWSPublisher;
import com.github.datnguyenzzz.dto.AWSJob;

@Component("awsKinesisPublisher")
public class AWSKinesisPublisher implements AWSPublisher {

    private final Logger logger = LoggerFactory.getLogger(AWSKinesisPublisher.class);

    @Override
    public void publish(AWSJob awsJob) {
        // TODO Auto-generated method stub
        logger.info("Using AWS Kinesis to publish message !!!");
        
    }
    
}
