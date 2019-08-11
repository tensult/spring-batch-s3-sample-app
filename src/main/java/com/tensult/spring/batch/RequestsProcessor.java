package com.tensult.spring.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.tensult.spring.batch.types.BatchRequest;

@StepScope
public class RequestsProcessor implements ItemProcessor<BatchRequest, BatchRequest> {

	@Override
	public BatchRequest process(final BatchRequest batchRequest) throws Exception {
		// TODO: We can put our custom logic to handle the batch request
		batchRequest.setResponseBody("RequestsProcessor started for " + batchRequest.getCurrentS3Object().getKey()
				+ " and currentBatchNumber:" + batchRequest.getBatchNumber());
		return batchRequest;
	}
}
