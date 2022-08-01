package com.github.datnguyenzzz.Interfaces;

import java.util.List;
import java.util.Map;

import com.github.datnguyenzzz.dto.JobDefinition;

public interface CronJobProvider {
    Map<String, List<JobDefinition>> getDefinition();
}
