package com.github.datnguyenzzz.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobListDefinition;

@Component
public class CronJobLocalProvider implements CronJobProvider {

    @Autowired
    private CronJobConfiguration config;

    public JobListDefinition getDefinition() {

        String locationFile = this.config.getCronJobDefinitionFile();

        return null;
    }
}
