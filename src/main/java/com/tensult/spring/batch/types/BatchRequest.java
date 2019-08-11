package com.tensult.spring.batch.types;

import com.amazonaws.services.s3.model.S3ObjectId;

/**
 * S3 objects processed by dividing each object into multiple {@link #BatchRequest(String, S3ObjectId, int, boolean, boolean)}
 * @author dev@tensult.com
 *
 */
public class BatchRequest {

	private String requestBody;
	private String responseBody;
	private S3ObjectId currentS3Object;
	/**
	 * Batch request number with in the currentS3Object
	 */
	private int batchNumber;
	/**
	 * For tracking if this is the last batch request for the currentS3Object 
	 */
	private boolean lastBatch;
	/**
	 * For tracking if this is the last Object in the S3 Folder
	 */
	private boolean lastS3Object;

	public BatchRequest(String requestBody, S3ObjectId currentS3Object, int batchNumber, boolean lastS3Object,
			boolean lastBatch) {
		this.requestBody = requestBody;
		this.currentS3Object = currentS3Object;
		this.lastBatch = lastBatch;
		this.lastS3Object = lastS3Object;
		this.batchNumber = batchNumber;
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
				+ currentS3Object + ", batchNumber=" + batchNumber + ", lastBatch=" + lastBatch + ", lastS3Object="
				+ lastS3Object + "]";
	}

}
