package com.github.datnguyenzzz.dto;

import lombok.Getter;
import lombok.Setter;

public class HealthyStatus {

    @Getter @Setter
    private int healthCheckFrequency;

    @Getter @Setter
    private int consecutiveError;

    @Getter @Setter
    private double rateError;

    @Getter @Setter
    private int totalJobs;

    @Getter @Setter
    private int successJobs;

    @Getter @Setter
    private int failureJobs;
}
