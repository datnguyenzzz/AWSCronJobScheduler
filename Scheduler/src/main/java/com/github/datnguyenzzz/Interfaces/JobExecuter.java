package com.github.datnguyenzzz.Interfaces;

import org.quartz.JobDetail;

public interface JobExecuter {
    void execute(JobDetail job);
}
