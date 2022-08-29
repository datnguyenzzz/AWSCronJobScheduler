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
