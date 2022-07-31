package com.github.datnguyenzzz.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class JobDefinition {
    
    @Getter @Setter
    private String cronTrigger;

    @Getter @Setter
    private Map<Object, Object> message;

    @Getter @Setter
    private String originService;

    @Getter @Setter
    private String destinationService;

    @Getter @Setter
    private String lambdaAction;
}
