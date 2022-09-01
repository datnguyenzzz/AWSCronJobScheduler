package com.github.datnguyenzzz.Components;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.github.datnguyenzzz.Exceptions.*;

import lombok.Getter;

@Component
public class CronJobConfiguration {

    @Getter
    private String cronJobProvider;
    
    @Getter
    private String cronJobDefinitionFile;

    @Getter
    private int healthCheckTimeWindow = 10;

    @Getter
    private int consecutiveErrorThreshold = -1;

    @Getter
    private int rateErrorThreshold = -1;

    @Getter
    private int numSchedulerEngines = 2;

    @Getter
    private String awsRegion;

    @Getter
    private String awsAccessKey;

    @Getter
    private String awsSecretKey;

    @Getter
    private String awsCredentialFile;

    private final Logger logger = LoggerFactory.getLogger(CronJobConfiguration.class);

    @PostConstruct
    public void init() throws Exception {

        // provider type
        this.cronJobProvider = System.getenv("CRON_JOB_PROVIDER");

        if (this.cronJobProvider == null
            || this.cronJobProvider.equals("")) {
            this.logger.error("Missing cron job provider");
            String msg = "Cron job provider is missing";
            throw new MissingEnviromentVariablesException(msg);
        }

        //location path
        this.cronJobDefinitionFile = System.getenv("CRON_JOB_LOCATION");
        
        if (this.cronJobDefinitionFile == null
            || this.cronJobDefinitionFile.equals("")) {
            this.logger.error("Missing cron job file");
            String msg = "Cron job definition file is missing";
            throw new MissingEnviromentVariablesException(msg);
        }

        //health check time
        String healthCheckMinute = System.getenv("HEALTH_CHECK_FREQUENCY");

        if (healthCheckMinute != null && !healthCheckMinute.equals("")) 
            this.healthCheckTimeWindow = Integer.parseInt(healthCheckMinute);

        //consecutiveError
        String consecutiveError = System.getenv("CONSECUTIVE_ERROR_POSTPONE_THRESHOLD");
        if (consecutiveError != null && !healthCheckMinute.equals(""))
            this.consecutiveErrorThreshold = Integer.parseInt(consecutiveError);

        //rateError
        String rateError = System.getenv("PERCENTAGE_ERROR_POSTPONE_THRESHOLD");
        if (rateError != null && !rateError.equals(""))
            this.rateErrorThreshold = Integer.parseInt(rateError);

        //aws credential
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

        //num scheduler engines
        this.numSchedulerEngines = Integer.parseInt(System.getenv("NUM_SCHEDULER_ENGINES"));
        
    }

}
