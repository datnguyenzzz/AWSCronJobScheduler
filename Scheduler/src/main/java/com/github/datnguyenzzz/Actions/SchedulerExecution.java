package com.github.datnguyenzzz.Actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SchedulerExecution {
    
    private final Logger logger = LoggerFactory.getLogger(SchedulerExecution.class);

    public SchedulerExecution() {}

    public void start() {
        logger.info("Start scheduler execution ...");
    }
}
