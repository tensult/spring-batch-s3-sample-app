# spring-batch-s3-sample-app
Reads CSV files stored S3 folder in a bucket one by one and process them (Like calling a third party API) and writes the responses to another subdirectory called responses and then process the responses to generate summary file.
## S3 folder structure
* Requests: s3://<bucket-name>/<folder-path>/requests
* Processed requests: s3://<bucket-name>/<folder-path>/processed (once request file is processed it will be moved to this folder)
* Responses: s3://<bucket-name>/<folder-path>/responses (this will be created)
* Summary file: s3://<bucket-name>/<folder-path>/summary.txt
## Rest API
This is for triggering and getting status of the jobs once the application is started.
* Triggering: http://localhost:8080/start?numRecordsInBatch=10&s3Folder=<Url Encoded S3 folder path in format s3://bucket-name/folder-path>
* Status: http://localhost:8080/status?numRecordsInBatch=10&s3Folder=<Url Encoded S3 folder path in format s3://bucket-name/folder-path> (should be called with same parameters used for triggering the job)
