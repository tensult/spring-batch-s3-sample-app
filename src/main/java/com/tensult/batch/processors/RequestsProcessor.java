
package com.tensult.batch.processors;


import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.tensult.service.APIService;
import com.tensult.types.BatchRequest;
import com.tensult.utils.CSVUtils;

@StepScope
public class RequestsProcessor implements ItemProcessor<BatchRequest, BatchRequest> {

	@Autowired
	private APIService apiService;

	@Override
	public BatchRequest process(final BatchRequest apiBatchRequest) throws Exception {
		System.out.println("RequestsProcessor for " + apiBatchRequest.getCurrentS3Object().getKey());

		if (apiBatchRequest.getBatchSize() == 0 || StringUtils.isBlank(apiBatchRequest.getRequestBody())) {
			return apiBatchRequest;
		}

		String jsonBodyasString = CSVUtils.toJSONString(apiBatchRequest.getRequestBody());

		String apiResponse = apiService.getAPIResponse(jsonBodyasString);
		apiBatchRequest.setResponseBody(apiResponse);
		return apiBatchRequest;
	}

}
