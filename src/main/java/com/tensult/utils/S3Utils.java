package com.tensult.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Utils {

	private static final Pattern s3UrlPattern = Pattern.compile("s3://([A-Za-z0-9_.-]+)/?(.*)");

	public static String getObjectContents(AmazonS3 s3Client, String bucketName, String objectKey) throws IOException {
		return StreamUtils.copyToString(getObjectStream(s3Client, bucketName, objectKey), Charset.forName("UTF-8"));
	}

	public static S3ObjectInputStream getObjectStream(AmazonS3 s3Client, String bucketName, String objectKey) {
		final S3Object s3Object = s3Client.getObject(bucketName, objectKey);
		return s3Object.getObjectContent();
	}

	public static S3ObjectId getS3ObjectId(String s3Url) {
		Matcher matcher = s3UrlPattern.matcher(s3Url);
		if (!matcher.matches()) {
			return null;
		}
		String s3BucketName = matcher.group(1);
		String s3ObjectKey = StringUtils.defaultString(StringUtils.removeEnd(matcher.group(2), "/"), StringUtils.EMPTY);
		return new S3ObjectId(s3BucketName, s3ObjectKey);
	}
}
