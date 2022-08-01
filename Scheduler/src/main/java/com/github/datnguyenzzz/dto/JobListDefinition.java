package com.github.datnguyenzzz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class JobListDefinition {
    
    @Getter @Setter
    private List<AWSJob> jobList;
}
