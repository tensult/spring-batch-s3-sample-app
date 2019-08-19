package com.tensult.types;

import com.amazonaws.services.s3.model.S3ObjectId;

public class BatchRequest {

	private String requestBody;
	private String responseBody;
	private S3ObjectId currentS3Object;
	private int batchNumber;
	private int batchSize;
	private boolean lastBatch;
	private boolean lastS3Object;

	public BatchRequest(String requestBody, S3ObjectId currentS3Object, int batchNumber, int batchSize,
			boolean lastS3Object, boolean lastBatch) {
		this.requestBody = requestBody;
		this.currentS3Object = currentS3Object;
		this.lastBatch = lastBatch;
		this.lastS3Object = lastS3Object;
		this.batchNumber = batchNumber;
		this.batchSize = batchSize;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public S3ObjectId getCurrentS3Object() {
		return currentS3Object;
	}

	public void setCurrentS3Object(S3ObjectId currentS3Object) {
		this.currentS3Object = currentS3Object;
	}

	public boolean isLastBatch() {
		return lastBatch;
	}

	public void setLastBatch(boolean lastBatch) {
		this.lastBatch = lastBatch;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

	public boolean isLastS3Object() {
		return lastS3Object;
	}

	public void setLastS3Object(boolean lastS3Object) {
		this.lastS3Object = lastS3Object;
	}

	@Override
	public String toString() {
		return "BatchRequest [requestBody=" + requestBody + ", responseBody=" + responseBody + ", currentS3Object="
				+ currentS3Object + ", batchNumber=" + batchNumber + ", batchSize=" + batchSize + ", lastBatch="
				+ lastBatch + ", lastS3Object=" + lastS3Object + "]";
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

}
