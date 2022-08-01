package com.github.datnguyenzzz.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class JobListDefinition {
    
    @Getter @Setter
    private List<AWSJob> jobList;

    @Getter
    private Map<String, AWSJob> jobHashMap = new HashMap<>();
    @Getter
    private Map<String, List<String>> jobExecutionOrder = new HashMap<>();

    public void updateRelation() {
        for (AWSJob job: jobList) {
            String jobName = job.getName();
            this.jobHashMap.put(jobName, job);
        }

        for (AWSJob job: jobList) {
            if (job.getAfterJobDone()==null) continue;
            String jobName = job.getName();
            String jobBefore = job.getAfterJobDone();

            if (!this.jobExecutionOrder.containsKey(jobBefore))
                this.jobExecutionOrder.put(jobBefore, new ArrayList<>());

            this.jobExecutionOrder.get(jobBefore).add(jobName);
        }
    }
    
}
