package com.tensult.spring.batch.configs;


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
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.tensult.spring.batch.RequestsProcessor;
import com.tensult.spring.batch.RequestsStepListener;
import com.tensult.spring.batch.ResponsesProcessor;
import com.tensult.spring.batch.ResponsesStepListener;
import com.tensult.spring.batch.ResponsesWriter;
import com.tensult.spring.batch.S3CSVObjectsReader;
import com.tensult.spring.batch.SummaryWriter;
import com.tensult.spring.batch.types.BatchRequest;

/**
 * Batch job Spring beans configuration
 * @author Dilip <dev@tensult.com>
 */
@Configuration
@EnableAsync
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private ApplicationContext appContext;

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
        		.<BatchRequest, BatchRequest> chunk(1)
            .reader(reader())
            .processor(responseProcessor())
            .writer(summaryWriter())
            .listener(responsesStepLister())
            .build();
    }
}
