package com.tensult.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class S3ObjectsBatchApplication {

	public static void main(String[] args) throws UnexpectedInputException, ParseException, Exception {
		SpringApplication.run(S3ObjectsBatchApplication.class, args);
	}
}
