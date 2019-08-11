package com.tensult.spring.batch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

public class ResponsesStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		String s3Folder = stepExecution.getJobParameters().getString("s3Folder");
		String responsesS3Folder = StringUtils.removeEnd(s3Folder, "/") + "/responses";
		jobContext.put("s3Folder", responsesS3Folder);
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

}
