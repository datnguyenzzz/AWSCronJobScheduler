package com.github.datnguyenzzz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.datnguyenzzz.Actions.HealthCheck;
import com.github.datnguyenzzz.Actions.SchedulerExecution;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    private SchedulerExecution schedulerExecution;

    @Autowired
    private HealthCheck healthCheck;

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        this.schedulerExecution.start();
        this.healthCheck.start();
    }
}
