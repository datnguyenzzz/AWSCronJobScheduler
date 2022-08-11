package com.github.datnguyenzzz.Components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Contains Health Check properties - sucesses, failures, ....
 * Produce successes and failure concurrent job
 */
@Component
public class HealthCheckEngine {

    private final static Logger logger = LoggerFactory.getLogger(HealthCheckEngine.class);

    public void run() {
        logger.info("Health Check engine is running ....");
    }
}
