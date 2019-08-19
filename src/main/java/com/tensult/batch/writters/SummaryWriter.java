package com.tensult.batch.writters;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.tensult.types.SummaryReport;
import com.tensult.utils.S3Utils;

@Component
@StepScope
public class SummaryWriter implements ItemWriter<SummaryReport> {

	@Value("#{jobParameters[s3Folder]}")
	private String s3Folder;

	@Autowired
	private AmazonS3 s3Client;

	private SummaryReport totalSummaryReport = new SummaryReport();
	
	@Override
	public void write(List<? extends SummaryReport> summaryReports) throws Exception {
		if (summaryReports.size() > 1) {
			throw new RuntimeException("Expecting chunk size to be 1");
		}
		SummaryReport summaryReport = summaryReports.get(0);
		
		addSummaryToTotal(summaryReport);
		
		if (summaryReport.getBatchRequest().isLastBatch() && 
				summaryReport.getBatchRequest().isLastS3Object()) {
			S3ObjectId s3FolderObjectId = S3Utils.getS3ObjectId(s3Folder);
			s3Client.putObject(s3FolderObjectId.getBucket(), s3FolderObjectId.getKey() + "/summary.txt", totalSummaryReport.toString());
		}
	}
	
	private void addSummaryToTotal(SummaryReport summaryReport) {
		totalSummaryReport.setFailedRequestCount(totalSummaryReport.getFailedRequestCount() + summaryReport.getFailedRequestCount());
		totalSummaryReport.setSucessfulRequestCount(totalSummaryReport.getSuucessfulRequestCount() + summaryReport.getSuucessfulRequestCount());
		totalSummaryReport.setTotalRequestCount(totalSummaryReport.getTotalRequestCount() + summaryReport.getTotalRequestCount());
		for( Entry<String, Integer> errorCount:summaryReport.getErrorCounts().entrySet()) {
			int newErrorCountValue = errorCount.getValue() + MapUtils.getIntValue(totalSummaryReport.getErrorCounts(), errorCount.getKey(), 0);
			totalSummaryReport.getErrorCounts().put(errorCount.getKey(), newErrorCountValue);
		}
	}

}
