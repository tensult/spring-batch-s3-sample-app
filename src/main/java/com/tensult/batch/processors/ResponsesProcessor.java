package com.tensult.batch.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.tensult.types.APIResponse;
import com.tensult.types.BatchRequest;
import com.tensult.types.SummaryReport;
import com.tensult.utils.CSVUtils;

@StepScope
public class ResponsesProcessor implements ItemProcessor<BatchRequest, SummaryReport> {

	@Override
	public SummaryReport process(final BatchRequest batchRequest) throws Exception {
		System.out.println("ResponsesProcessor for " + batchRequest.getCurrentS3Object().getKey());
		List<APIResponse> apiResponses = CSVUtils.toList(batchRequest.getRequestBody(), APIResponse.class);
		return getSummaryReport(apiResponses, batchRequest);
	}

	private SummaryReport getSummaryReport(List<APIResponse> apiResponses, BatchRequest batchRequest) {
		SummaryReport summaryReport = new SummaryReport(batchRequest);
		Map<String, Integer> errorCounts = new HashMap<String, Integer>();
		int totalRequests = 0;
		int failedRequests = 0;
		int successRequests = 0;

		for (APIResponse apiResponse : apiResponses) {
			if (apiResponse.isFailed()) {
				errorCounts.put(apiResponse.getMessage(),
						MapUtils.getIntValue(errorCounts, apiResponse.getMessage(), 0) + 1);
				failedRequests += apiResponse.getBatchSize();
			} else {
				successRequests += apiResponse.getBatchSize();
			}
			totalRequests += apiResponse.getBatchSize();
		}
		summaryReport.setFailedRequestCount(failedRequests);
		summaryReport.setSucessfulRequestCount(successRequests);
		summaryReport.setTotalRequestCount(totalRequests);
		summaryReport.setErrorCounts(errorCounts);
		return summaryReport;
	}

}
