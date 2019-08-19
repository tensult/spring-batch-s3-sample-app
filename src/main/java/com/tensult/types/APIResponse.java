package com.tensult.types;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class APIResponse {

	private String status;
	private String message;
	private int batchSize;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	@JsonIgnore
	public boolean isFailed() {
		return !StringUtils.equalsIgnoreCase(status, "success");
	}
	
	@Override
	public String toString() {
		return "APIResponse [status=" + status + ", message=" + message + ", batchSize=" + batchSize + "]";
	}
}
