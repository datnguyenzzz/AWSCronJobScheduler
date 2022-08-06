package com.github.datnguyenzzz.dto;

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

}
