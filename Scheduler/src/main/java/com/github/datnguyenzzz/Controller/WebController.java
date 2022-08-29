package com.github.datnguyenzzz.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.datnguyenzzz.Components.QuartzJobGenerator;
import com.github.datnguyenzzz.Handlers.AddJobHandler;
import com.github.datnguyenzzz.Handlers.HealthCheckHandler;
import com.github.datnguyenzzz.dto.AWSJob;
import com.github.datnguyenzzz.dto.HealthStatus;

@RestController
public class WebController {

    @Autowired
    private HealthCheckHandler healthCheckHandler;

    @Autowired
    private AddJobHandler addJobHandler;

    @Autowired
    private QuartzJobGenerator quartzJobGenerator;

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
            this.addJobHandler.addNewJob(awsJob);

            // find already stored job within scheduler storage
            String jobBeforeName = awsJob.getAfterJobDone();
            JobKey jobBeforeKey = this.quartzJobGenerator.genJobKey(jobBeforeName);
            JobDetail jobBeforeDetail = this.addJobHandler.getJobDetailFromKey(jobBeforeKey);

            //
            return null;
        }
        catch (SchedulerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}
