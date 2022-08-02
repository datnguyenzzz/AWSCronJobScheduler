package com.github.datnguyenzzz.Factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Components.CronJobConfiguration;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobListDefinition;

/**
 * Currently have bean:
 * - cronJobLocalProvider
 * - cronJobS3Provider
 * 
 * implement interface CronJobProvider
 */
@Component("cronJobProviderFactory")
@Scope("prototype")
public class CronJobProviderFactory implements FactoryBean<CronJobProvider> {

    private final String CRONJOB_PROVIDER = "CronJobProvider";

    @Autowired
    private CronJobConfiguration config;

    @Autowired
    private ApplicationContext ctx;

    private final Logger logger = LoggerFactory.getLogger(CronJobProviderFactory.class);

    @Override
    public CronJobProvider getObject() throws Exception {
        String providerType = config.getCronJobProvider().toLowerCase();
        if (providerType.equals("")) {
            logger.info("Provider type is missing");
            return null;
        }
        else if (providerType.equals("local")
                || providerType.equals("S3")) {
            logger.info("Read job definition from " + providerType);
            return (CronJobProvider) ctx.getBean(getTargetProvider(providerType));
        }
        else {
            logger.info("Provider is not supported");
            return null;
        }
    }

    private String getTargetProvider(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name.toLowerCase());
        sb.append(CRONJOB_PROVIDER);
        return sb.toString();
    }

    @Override
    public Class<?> getObjectType() {
        return JobListDefinition.class;
    }

}
