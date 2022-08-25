package com.github.datnguyenzzz.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class HealthStatus {

    @Getter @Setter
    private String jobName;

    @Getter @Setter
    private int jobFired;

    @Getter @Setter
    private int jobMisfired;

    @Getter @Setter
    private int jobCompleted;

    @Getter @Setter
    private String jobStatus;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t Name : " + this.jobName);

        sb.append("\t Fired: " + this.jobFired);
        sb.append("\t Misfired: " + this.jobMisfired);
        sb.append("\t Completed: " + this.jobCompleted);
        sb.append("\t Status: " + this.jobCompleted);

        return sb.toString();
    }
}
