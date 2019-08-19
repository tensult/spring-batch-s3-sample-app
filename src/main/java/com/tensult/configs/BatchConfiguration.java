package com.tensult.configs;


import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.tensult.batch.listeners.RequestsStepListener;
import com.tensult.batch.listeners.ResponsesStepListener;
import com.tensult.batch.processors.RequestsProcessor;
import com.tensult.batch.processors.ResponsesProcessor;
import com.tensult.batch.readers.S3CSVObjectsReader;
import com.tensult.batch.writters.ResponsesWriter;
import com.tensult.batch.writters.SummaryWriter;
import com.tensult.types.BatchRequest;
import com.tensult.types.SummaryReport;

@Configuration
@EnableAsync
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private ApplicationContext appContext;

    @Autowired
    public S3CSVObjectsReader reader() throws IOException {
    	return appContext.getBean(S3CSVObjectsReader.class);
    }
    
    @Bean
    public RequestsProcessor requestProcessor() {
        return new RequestsProcessor();
    }
    
    @Bean
    public ResponsesProcessor responseProcessor() {
        return new ResponsesProcessor();
    }
    
    @Bean
    public RequestsStepListener requestsStepLister() {
    	return new RequestsStepListener();
    }
    
    @Bean
    public ResponsesStepListener responsesStepLister() {
    	return new ResponsesStepListener();
    }
    
    @Bean
    public ResponsesWriter responsesWriter() {
        return new ResponsesWriter();
    }
    
    public SummaryWriter summaryWriter() {
    	return appContext.getBean(SummaryWriter.class);
    }


    @Bean
    public Job importUsersJob() throws IOException {
        return jobBuilderFactory.get("importUsersJob")
            .incrementer(new RunIdIncrementer())
            .flow(pushMembersDataToAPI())
            .next(generateSummary())
            .end()
            .build();
    }
    
    @Bean
    AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
    }

    @Bean
    public Step pushMembersDataToAPI() throws IOException {
        return stepBuilderFactory.get("pushMembersDataToAPI")
        		.<BatchRequest, BatchRequest> chunk(1)
            .reader(reader())
            .processor(requestProcessor())
            .writer(responsesWriter())
            .listener(requestsStepLister())
            .build();
    }
    
    @Bean
    public Step generateSummary() throws IOException {
        return stepBuilderFactory.get("generateSummary")
        		.<BatchRequest, SummaryReport> chunk(1)
            .reader(reader())
            .processor(responseProcessor())
            .writer(summaryWriter())
            .listener(responsesStepLister())
            .build();
    }
}
