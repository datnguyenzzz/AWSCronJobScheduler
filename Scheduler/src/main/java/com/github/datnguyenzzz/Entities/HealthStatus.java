package com.github.datnguyenzzz.Entities;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class HealthStatus {

    @Getter @Setter
    private LocalDateTime time;

    @Getter @Setter
    private String jobName;

    @Getter @Setter
    private int jobFired;

    @Getter @Setter
    private int jobMisfired;

    @Getter @Setter
    private int jobCompleted;

    @Getter @Setter
    private int jobFailed;

    @Getter @Setter
    private String jobStatus;

    public HealthStatus(LocalDateTime time, String jobName, int jobFired, int jobMisFired, int jobCompleted, int jobFailed, String jobStatus) {
        this.time = time;
        this.jobName = jobName;
        this.jobFired = jobFired;
        this.jobMisfired = jobMisFired;
        this.jobCompleted = jobCompleted;
        this.jobFailed = jobFailed;
        this.jobStatus = jobStatus;
    }

    /*
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t Name : " + this.jobName);

        sb.append("\t Fired: " + this.jobFired);
        sb.append("\t Misfired: " + this.jobMisfired);
        sb.append("\t Completed: " + this.jobCompleted);
        sb.append("\t Status: " + this.jobCompleted);

        return sb.toString();
    }*/
}
