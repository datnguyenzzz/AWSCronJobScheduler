package com.github.datnguyenzzz.Components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.AWSPublisher;
import com.github.datnguyenzzz.dto.AWSJob;

@Component("awsSQSPublisher")
public class AWSSQSPublisher implements AWSPublisher {

    private final Logger logger = LoggerFactory.getLogger(AWSSQSPublisher.class);

    @Override
    public void publish(AWSJob awsJob) {
        logger.info("Using AWS SQS to publish message !!!");
    }

    @Override
    public void publish(String content) {
        logger.info("Using AWS SQS to publish message - " + content + " !!!");
    }
}