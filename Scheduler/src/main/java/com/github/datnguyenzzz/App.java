package com.github.datnguyenzzz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.datnguyenzzz.Actions.HealthCheckingAction;
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
    private HealthCheckingAction healthChecking;

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //health check service is more likely to start first 
        this.healthChecking.start();
        this.schedulerExecution.start();
    }
}
