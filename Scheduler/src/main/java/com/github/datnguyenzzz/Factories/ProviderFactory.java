package com.github.datnguyenzzz.Factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component("providerFactory")
public class ProviderFactory implements FactoryBean<CronJobProvider> {

    @Autowired
    private CronJobConfiguration config;

    @Autowired
    private ApplicationContext ctx;

    private final Logger logger = LoggerFactory.getLogger(ProviderFactory.class);

    @Override
    public CronJobProvider getObject() throws Exception {
        String providerType = config.getCronJobProvider().toLowerCase();
        if (providerType.equals("")) {
            logger.info("Provider type is missing");
            return null;
        }
        else if (providerType.equals("local")) {
            logger.info("Read job definition from LOCAL");
            return (CronJobProvider) ctx.getBean("cronJobLocalProvider");
        }
        else if (providerType.equals("S3")) {
            logger.info("Read job definition from S3");
            return (CronJobProvider) ctx.getBean("cronJobLocalProvider");
        }
        else {
            logger.info("Provider is not supported");
            return null;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return JobListDefinition.class;
    }

}