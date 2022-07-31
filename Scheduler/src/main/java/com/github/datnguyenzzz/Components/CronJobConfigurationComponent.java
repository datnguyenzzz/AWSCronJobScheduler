package com.github.datnguyenzzz.Components;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.github.datnguyenzzz.Exceptions.*;

import lombok.Getter;

@Component
public class CronJobConfigurationComponent {
    
    @Getter
    private String cronJobDefinitionFile;

    @Getter
    private int healthCheckFrequency = 10;

    @Getter
    private int consecutiveErrorThreshold = -1;

    @Getter
    private int rateErrorThreshold = -1;

    @Getter
    private String awsRegion;

    @Getter
    private String awsAccessKey;

    @Getter
    private String awsSecretKey;

    @Getter
    private String awsCredentialFile;

    private final Logger logger = LoggerFactory.getLogger(CronJobConfigurationComponent.class);

    @PostConstruct
    public void init() throws Exception {
        this.cronJobDefinitionFile = System.getenv("CRON_JOB_LOCATION");
        
        if (this.cronJobDefinitionFile == null
            || this.cronJobDefinitionFile.equals("")) {
            this.logger.error("Missing cron job file");
            String msg = "Cron job definition file is missing";
            throw new MissingEnviromentVariablesException(msg);
        }

        String healthCheckMinute = System.getenv("HEALTH_CHECK_FREQUENCY");

        if (healthCheckMinute != null && !healthCheckMinute.equals("")) 
            this.healthCheckFrequency = Integer.parseInt(healthCheckMinute);

        String consecutiveError = System.getenv("CONSECUTIVE_ERROR_POSTPONE_THRESHOLD");
        if (consecutiveError != null && !healthCheckMinute.equals(""))
            this.consecutiveErrorThreshold = Integer.parseInt(consecutiveError);

        String rateError = System.getenv("PERCENTAGE_ERROR_POSTPONE_THRESHOLD");
        if (rateError != null && !rateError.equals(""))
            this.rateErrorThreshold = Integer.parseInt(rateError);

        this.awsRegion = System.getenv("AWS_REGION");
        this.awsAccessKey = System.getenv("AWS_ACCESS_KEY");
        this.awsSecretKey = System.getenv("AWS_SECRET_KEY");
        this.awsCredentialFile = System.getenv("AWS_CREDENTIALS_TARGET");

        boolean awsCreVarsMissing = (this.awsRegion==null || this.awsAccessKey==null || this.awsSecretKey==null);
        boolean awsCreFileMissing = (this.awsCredentialFile==null);

        if (awsCreFileMissing && awsCreVarsMissing) {
            this.logger.error("Missing aws credentials");
            String msg = "AWS Credentials is missing";
            throw new MissingEnviromentVariablesException(msg);
        }
        
    }

}
