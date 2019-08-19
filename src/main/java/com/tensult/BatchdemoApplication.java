package com.tensult;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class BatchdemoApplication {

	public static void main(String[] args) throws UnexpectedInputException, ParseException, Exception {
		SpringApplication.run(BatchdemoApplication.class, args);
	}
}
