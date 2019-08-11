package com.tensult.spring.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.AmazonS3;
import com.tensult.spring.batch.types.BatchRequest;

@StepScope
public class ResponsesWriter implements ItemWriter<BatchRequest> {

	@Autowired
	private AmazonS3 s3Client;
	
	// Batch request Responses for an S3 object
	private List<String> responses = new ArrayList<String>();
	
	@Override
	public void write(List<? extends BatchRequest> batchRequests) throws Exception {
		if(batchRequests.size() > 1) {
			throw new RuntimeException("Expecting chunk size to be 1");
		}
		BatchRequest batchRequest = batchRequests.get(0);
        responses.add(batchRequest.getResponseBody());
        
        if(batchRequest.isLastBatch()) {
        	// Processed S3 objects are moved processed folder and batch request responses are stored responses folder
        	String responseObjectKey = batchRequest.getCurrentS3Object().getKey().replace("requests", "responses");
        	String processedObjectKey = batchRequest.getCurrentS3Object().getKey().replace("requests", "processed");
        	s3Client.putObject(batchRequest.getCurrentS3Object().getBucket(), responseObjectKey, responses.toString());
        	s3Client.copyObject(batchRequest.getCurrentS3Object().getBucket(), 
        			batchRequest.getCurrentS3Object().getKey(),
        			batchRequest.getCurrentS3Object().getBucket(), processedObjectKey);
        	s3Client.deleteObject(batchRequest.getCurrentS3Object().getBucket(), 
        			batchRequest.getCurrentS3Object().getKey());
        	System.out.println("ResponsesWriter finished for "+ batchRequest.getCurrentS3Object().getKey());
        	responses.clear();
        }
	}

}
