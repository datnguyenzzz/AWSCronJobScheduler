package com.github.datnguyenzzz.Actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;

@Component
public class HealthCheck {

    @Autowired
    private CronJobConfiguration config;
    
    private final static Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    public void start() {

        int healthCheckFrequency = config.getHealthCheckFrequency();

        logger.info("Health checking service is periodically starting every "+ healthCheckFrequency + " second");
    }
}
