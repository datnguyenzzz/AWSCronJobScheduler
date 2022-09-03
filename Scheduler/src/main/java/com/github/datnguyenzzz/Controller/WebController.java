package com.github.datnguyenzzz.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.datnguyenzzz.Entities.AWSJob;
import com.github.datnguyenzzz.Entities.HealthStatus;
import com.github.datnguyenzzz.Services.AddJobServiceImpl;
import com.github.datnguyenzzz.Services.HealthCheckService;

@RestController
public class WebController {

    @Autowired
    private HealthCheckService healthCheckHandler;

    @Autowired
    private AddJobServiceImpl addJobService;

    @GetMapping("/")
    public String index() throws Exception {
        return "Okay!!";
    }

    @GetMapping("/hs")
    public ResponseEntity<List<HealthStatus>> getAllHealthStatus() {
        Map<Integer, HealthStatus> mapHS = this.healthCheckHandler.getAllHealthStatus();
        List<HealthStatus> listAllHS = mapHS.entrySet().stream().map(x -> x.getValue()).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(listAllHS);
    } 

    @PostMapping("/addNewJob")
    public ResponseEntity<String> addNewJob(@RequestBody AWSJob awsJob) {
        try {
            //add job to storage
            this.addJobService.addNewJob(awsJob);
            //if sequential is applied
            String jobBeforeName = awsJob.getAfterJobDone();
            if (jobBeforeName != null && !jobBeforeName.equals(""))
                this.addJobService.addSequentialTrigger(jobBeforeName, awsJob);
            //schedule this one
            this.addJobService.scheduleCurrentJob(awsJob);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Add job - " + awsJob.getName() + " successfully");
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}
