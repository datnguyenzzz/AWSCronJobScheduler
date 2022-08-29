# Cron job scheduler on Amazon Web Service

Automatically arrange based on specific predefined firing schedules and periodically check the health statuses ofcustomizable tasks, which involved Amazon Web Services.

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
#http://www.quartz-scheduler.org/documentation/2.4.0-SNAPSHOT/tutorials/tutorial-lesson-06.html
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
```
