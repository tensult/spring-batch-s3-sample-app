package com.tensult.spring.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tensult.spring.batch.types.BatchRequest;
import com.tensult.spring.batch.utils.S3Utils;

/**
 * Reads CSV files one by one from from S3 folder
 * @author Dilip <dev@tensult.com>
 *
 */
@Component
@StepScope
public class S3CSVObjectsReader implements ItemReader<BatchRequest> {

	private BufferedReader currentS3ObjectReader = null;
	private Iterator<S3ObjectSummary> s3ObjectSummaryIterator;
	private int numRecordsInBatch;
	private int currentBatchNumber = 0;
	private String headers;
	private S3ObjectId s3FolderObject;
	private S3ObjectId currentS3ObjectId;
	private ListObjectsV2Result currentS3ListObjectsResult;
	private AmazonS3 s3Client;

	@Autowired
	public S3CSVObjectsReader(@Value("#{jobExecutionContext[s3Folder]}") String s3FolderUrl,
			@Value("#{jobParameters[numRecordsInBatch]}") int numRecordsInBatch, @Autowired AmazonS3 s3Client)
			throws IOException {
		this.s3FolderObject = S3Utils.getS3ObjectId(s3FolderUrl);
		System.out.println(s3FolderObject);
		this.numRecordsInBatch = numRecordsInBatch;
		this.s3Client = s3Client;
	}

	private ListObjectsV2Request getListObjectsV2Request(String continuationToken) {
		return new ListObjectsV2Request().withBucketName(s3FolderObject.getBucket()).withPrefix(s3FolderObject.getKey())
				.withMaxKeys(100).withContinuationToken(continuationToken);
	}

	/**
	 * Lists the S3 folder and returns a S3 object to process one by one.
	 * @return S3ObjectId
	 */
	private S3ObjectId getNextS3Object() {
		if (s3ObjectSummaryIterator != null && s3ObjectSummaryIterator.hasNext()) {
			S3ObjectSummary s3ObjectSummary = s3ObjectSummaryIterator.next();
			return new S3ObjectId(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey());
		}

		if (currentS3ListObjectsResult != null && !currentS3ListObjectsResult.isTruncated()) {
			return null;
		}

		String s3ListObjectsContinuationToken = currentS3ListObjectsResult != null
				? currentS3ListObjectsResult.getNextContinuationToken()
				: null;
		currentS3ListObjectsResult = s3Client.listObjectsV2(getListObjectsV2Request(s3ListObjectsContinuationToken));
		s3ObjectSummaryIterator = currentS3ListObjectsResult.getObjectSummaries().stream().filter((s3ObjectSummaryObj) -> {
					return s3ObjectSummaryObj.getSize() > 0;
				}).collect(Collectors.toList()).iterator();
		return getNextS3Object();
	}

	private BufferedReader prepareS3ObjectReader() throws IOException {
		if (currentS3ObjectReader != null) {
			return currentS3ObjectReader;
		}

		currentS3ObjectId = getNextS3Object();
		if(currentS3ObjectId == null) {
			return null;
		}
		BufferedReader newS3ObjectReader = new BufferedReader(new InputStreamReader(
				S3Utils.getObjectStream(s3Client, currentS3ObjectId.getBucket(), currentS3ObjectId.getKey())));
		headers = newS3ObjectReader.readLine();
		currentBatchNumber = 0;
		System.out.println("S3Object reading started for " + currentS3ObjectId);

		return newS3ObjectReader;
	}

	private BatchRequest prepareAPIBatchRequest() throws IOException {

		if ((currentS3ObjectReader = prepareS3ObjectReader()) == null) {
			return null;
		}

		boolean lastS3Object = !currentS3ListObjectsResult.isTruncated() && !s3ObjectSummaryIterator.hasNext();

		boolean lastBatch = false;
		String lines = headers;

		for (int i = 0; i < numRecordsInBatch; i++) {
			String line = currentS3ObjectReader.readLine();
			if (line == null) {
				currentS3ObjectReader.close();
				currentS3ObjectReader = null;
				lastBatch = true;
				break;
			}
			lines = "\n" + line;
		}
		return new BatchRequest(lines, currentS3ObjectId, ++currentBatchNumber,  lastS3Object, lastBatch);
	}

	@Override
	public BatchRequest read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return prepareAPIBatchRequest();
	}

}
