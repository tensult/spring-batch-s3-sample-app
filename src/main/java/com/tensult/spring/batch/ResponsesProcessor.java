package com.tensult.spring.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.tensult.spring.batch.types.BatchRequest;

@StepScope
public class ResponsesProcessor implements ItemProcessor<BatchRequest, BatchRequest> {

	@Override
	public BatchRequest process(final BatchRequest apiBatchRequest) throws Exception {
		apiBatchRequest.setResponseBody("MembersResponseProcessor started for " + apiBatchRequest.getCurrentS3Object().getKey()
				+ " and currentBatchNumber:" + apiBatchRequest.getBatchNumber());
		return apiBatchRequest;

	}

}
