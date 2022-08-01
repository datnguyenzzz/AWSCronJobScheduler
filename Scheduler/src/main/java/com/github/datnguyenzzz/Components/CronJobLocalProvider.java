package com.github.datnguyenzzz.Components;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.datnguyenzzz.Exceptions.SystemException;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.Job;
import com.github.datnguyenzzz.dto.JobListDefinition;
import com.github.datnguyenzzz.dto.Message;

@Component
public class CronJobLocalProvider implements CronJobProvider {

    @Autowired
    private CronJobConfiguration config;

    private ObjectMapper mapper;

    private final Logger logger = LoggerFactory.getLogger(CronJobLocalProvider.class);

    @PostConstruct
    public void init() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public JobListDefinition getDefinition() {

        String locationFile = this.config.getCronJobDefinitionFile();
        logger.info("Read job definition from LOCAL file");
        try {
            File file = new File(locationFile);
            JobListDefinition definition = this.mapper.readValue(file, JobListDefinition.class);
            //loggin
            logger.info(locationFile);
            for (Job job : definition.getJobList()) {
                logger.info(job.getName());
                logger.info(job.getCronTrigger());
                logger.info(job.getLambdaActionFile());
                logger.info(job.getUsedService());
                logger.info(job.getAfterJobDone());
                if (job.getMessages()!=null) {
                    for (Message mes: job.getMessages()) {
                        logger.info(mes.getKey() + " - " + mes.getValue());
                    }
                }
                logger.info("----------------------------------------");
            }
            return definition;
        }
        catch (Exception ex) {
            throw new SystemException("can not read yaml file");
        }
    }
}
