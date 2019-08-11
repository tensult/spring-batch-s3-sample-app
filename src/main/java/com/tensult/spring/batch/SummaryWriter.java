package com.tensult.spring.batch;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.tensult.spring.batch.types.BatchRequest;
import com.tensult.spring.batch.utils.S3Utils;

@Component
@StepScope
public class SummaryWriter implements ItemWriter<BatchRequest> {

	@Value("#{jobParameters[s3Folder]}")
	private String s3Folder;

	@Autowired
	private AmazonS3 s3Client;

	private String summary = "";

	@Override
	public void write(List<? extends BatchRequest> batchRequests) throws Exception {
		if (batchRequests.size() > 1) {
			throw new RuntimeException("Expecting chunk size to be 1");
		}
		BatchRequest batchRequest = batchRequests.get(0);
		// TODO: we can put our own custom summary logic.
		summary = batchRequest.getResponseBody() + "";
		if (batchRequest.isLastBatch() && batchRequest.isLastS3Object()) {
			S3ObjectId s3FolderObjectId = S3Utils.getS3ObjectId(s3Folder);
			s3Client.putObject(s3FolderObjectId.getBucket(), s3FolderObjectId.getKey() + "/summary.txt", summary);
			System.out.println("SummaryWriter finished for " + batchRequest.getCurrentS3Object().getKey());
		}
	}

}
