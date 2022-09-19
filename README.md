# Cron job scheduler on Amazon Web Service

Automatically arrange based on specific predefined firing schedules and periodically check the health statuses ofcustomizable tasks, which involved Amazon Web Services.

## AWS actions (*will be updated more feature*) 

- Sent an event to AWS SQS 
- Sent an event to AWS Kinesis
- Schedule a custom java-coded job

## Required enviroment variables 

| Name | Example | Description |
| --- | --- | --- |
| CRON_JOB_PROVIDER | local | Type of storage where reside cronjob definition file. Current only support from local and S3 |
| CRON_JOB_LOCATION | /AWSCronJob/definition.yaml | Cronjob definition file path (or bucket name if storage is S3)
| HEALTH_CHECK_FREQUENCY | 5 | Health check will aggregate status of all jobs within schedule every 5 second |
| CONSECUTIVE_ERROR_POSTPONE_THRESHOLD | 5 | Scheduler will postpone job if it met consecutive error is higher than 5% |
| PERCENTAGE_ERROR_POSTPONE_THRESHOLD | 10 | Scheduler will postpone job if it met overall percentage error is higher than 10% |
| AWS_REGION | eu-north-1 | AWS Region where the services deployed
| AWS_ACCESS_KEY | | AWS Access key |
| AWS_SECRET_KEY | | AWS Secret key |
| AWS_CREDENTIALS_TARGET | *optional* | AWS credentials file |

## Cronjob definition file

Definiton of cron and message to be sent described in YAML format. Job can be fired based on cron definition or after certain job. 

> DISCLAIMER: Currently only support tree like architecture. In future will be support DAG type. 

```
#supported tree architecture only
jobList:
  - name: "Upload to SQS #1"
    cronTrigger: "0/4 * * * * ?"
    usedService: "SQS"
    messages:
      - key: "key-1"
        value: "value-1"
      - key: "key-2"
        value: "value-2"

  - name: "Upload to SQS #2"
    usedService: "SQS"
    afterJobDone: "Upload to SQS #1"
    lambdaActionFile: "abc/def/xyz.java"
  
  - name: "Upload to Kinesis # 4"
    usedService: "Kinesis"
    afterJobDone: "Upload to SQS #2"

  - name: "Upload to SQS #5"
    usedService: "SQS"
    afterJobDone: "Upload to SQS #2"
  
  - name: "Upload to Kinesis #3"
    cronTrigger: "0/5 * * * * ?"
    usedService: "Kinesis"
    lambdaActionFile: "xyz/ghi/dfc.java"

  - name: "Custom job #6"
    cronTrigger: "0/2 * * * * ?"
    usedService: "Custom"
    actionFile: "/AWSCronJob/CustomJobs/CustomJobImpl.java"
```

Plain java object (POJO) represent a job

```
public class AWSJob extends Object {
    
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
```

A Custom job must be implement interface ``` com.github.datnguyenzzz.Interfaces.CustomJob ```. An example of legitimate custom job:

```
package CronJobActionFiles;
import com.github.datnguyenzzz.Interfaces.CustomJob;

public class CustomJobImpl implements CustomJob {
    private void pre(String yell) {
        System.out.println("Void invoked - " + yell);
    }
    public void execute() {
        pre("THIS IS SPARTA !!!!");
        System.out.println("THIS IS CUSTOM JOB !!!");
    }
}
```


## REST Api list:

_Scheduler will automatically distributed a job to appropriate schedule engine_

- /hs : Return JSON health status of all job within scheduler
- /addNewJob : Add new job into scheduler

## Plan architecture

![alt text](https://raw.githubusercontent.com/datnguyenzzz/AWSCronJobScheduler/develop/Assets/design.png)