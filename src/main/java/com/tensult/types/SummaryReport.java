package com.tensult.types;

import java.util.HashMap;
import java.util.Map;

public class SummaryReport {
	private int totalRequestCount = 0;
	private int suucessfulRequestCount = 0;
	private int failedRequestCount = 0;
	private BatchRequest batchRequest;
	
	public SummaryReport() {
	}
	
	public SummaryReport(BatchRequest batchRequest) {
		this.batchRequest = batchRequest;
	}
	private Map<String, Integer> errorCounts =  new HashMap<String, Integer>();
	
	public int getTotalRequestCount() {
		return totalRequestCount;
	}
	public void setTotalRequestCount(int totalRequestCount) {
		this.totalRequestCount = totalRequestCount;
	}
	public int getSuucessfulRequestCount() {
		return suucessfulRequestCount;
	}
	public void setSucessfulRequestCount(int suucessfulRequestCount) {
		this.suucessfulRequestCount = suucessfulRequestCount;
	}
	public int getFailedRequestCount() {
		return failedRequestCount;
	}
	public void setFailedRequestCount(int failedRequestCount) {
		this.failedRequestCount = failedRequestCount;
	}
	public Map<String, Integer> getErrorCounts() {
		return errorCounts;
	}
	public void setErrorCounts(Map<String, Integer> errorCounts) {
		this.errorCounts.putAll(errorCounts);
	}
	
	public BatchRequest getBatchRequest() {
		return batchRequest;
	}
	public void setBatchRequest(BatchRequest batchRequest) {
		this.batchRequest = batchRequest;
	}
	
	@Override
	public String toString() {
		return "SummaryReport [totalRequestCount=" + totalRequestCount + ", suucessfulRequestCount="
				+ suucessfulRequestCount + ", failedRequestCount=" + failedRequestCount + ", batchRequest="
				+ batchRequest + ", errorCounts=" + errorCounts + "]";
	}
}
