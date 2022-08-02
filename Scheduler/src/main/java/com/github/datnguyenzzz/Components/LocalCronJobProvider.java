package com.github.datnguyenzzz.Components;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.datnguyenzzz.Exceptions.SystemException;
import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component("localCronJobProvider")
public class LocalCronJobProvider implements CronJobProvider {

    @Autowired
    private CronJobConfiguration config;

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public JobListDefinition getDefinition() {

        String locationFile = this.config.getCronJobDefinitionFile();
        try {
            File file = new File(locationFile);
            JobListDefinition definition = this.mapper.readValue(file, JobListDefinition.class);
            return definition;
        }
        catch (Exception ex) {
            throw new SystemException("can not read yaml file");
        }
    }
}
