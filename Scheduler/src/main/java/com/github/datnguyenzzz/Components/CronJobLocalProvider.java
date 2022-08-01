package com.github.datnguyenzzz.Components;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.datnguyenzzz.Interfaces.CronJobProvider;
import com.github.datnguyenzzz.dto.JobDefinition;

@Component
public class CronJobLocalProvider implements CronJobProvider {

    private CronJobConfigurationComponent config;

    @Autowired
    public CronJobLocalProvider(CronJobConfigurationComponent config) {
        this.config = config;
    }

    public Map<String, List<JobDefinition>> getDefinition() {

        String locationFile = this.config.getCronJobDefinitionFile();

        return null;
    }
}
