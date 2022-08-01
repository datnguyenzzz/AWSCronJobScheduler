package com.github.datnguyenzzz.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class AWSJob {
    
    @Getter @Setter
    private String name;

    @Getter @Setter
    private String cronTrigger;

    @Getter @Setter
    private List<Message> messages;

    @Getter @Setter
    private String usedService;

    @Getter @Setter
    private String afterJobDone;

    @Getter @Setter
    private String lambdaActionFile;
}
