package com.tensult.batch.writters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensult.constants.CommonConstants;
import com.tensult.types.APIResponse;
import com.tensult.types.BatchRequest;
import com.tensult.utils.CSVUtils;

@StepScope
public class ResponsesWriter implements ItemWriter<BatchRequest> {

	@Autowired
	private AmazonS3 s3Client;

	private List<APIResponse> responses = new ArrayList<APIResponse>();
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void write(List<? extends BatchRequest> batchRequests) throws Exception {
		if (batchRequests.size() > 1) {
			throw new RuntimeException("Expecting chunk size to be 1");
		}

		BatchRequest batchRequest = batchRequests.get(0);

		if (StringUtils.isNotBlank(batchRequest.getResponseBody())) {
			APIResponse response = objectMapper.readValue(batchRequest.getResponseBody(), APIResponse.class);
			response.setBatchSize(batchRequest.getBatchSize());
			responses.add(response);
		}

		if (batchRequest.isLastBatch()) {
			String responseObjectKey = batchRequest.getCurrentS3Object().getKey().replace("requests", "responses");
			String processedObjectKey = batchRequest.getCurrentS3Object().getKey().replace("requests", "processed");
				
			s3Client.putObject(batchRequest.getCurrentS3Object().getBucket(), responseObjectKey,
					CSVUtils.toCSVString(responses, CommonConstants.CSV_COLUMN_SEPARATOR));
			s3Client.copyObject(batchRequest.getCurrentS3Object().getBucket(),
					batchRequest.getCurrentS3Object().getKey(), batchRequest.getCurrentS3Object().getBucket(),
					processedObjectKey);
			s3Client.deleteObject(batchRequest.getCurrentS3Object().getBucket(),
					batchRequest.getCurrentS3Object().getKey());
			System.out.println("MemberDataAPIResponseWriter finished for " + batchRequest.getCurrentS3Object().getKey()
					+ " BatchSize:" + batchRequest.getBatchSize());
			responses.clear();
		}
	}
}
