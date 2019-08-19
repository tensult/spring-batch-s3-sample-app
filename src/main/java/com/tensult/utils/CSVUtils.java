package com.tensult.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.tensult.constants.CommonConstants;

public class CSVUtils {

	public static String toCSVString(Object obj, char separator) throws JsonProcessingException {
		JsonNode jsonTree = new ObjectMapper().valueToTree(obj);
		Builder csvSchemaBuilder = CsvSchema.builder();
		JsonNode firstObject = jsonTree.elements().next();
		firstObject.fieldNames().forEachRemaining(fieldName -> {
			csvSchemaBuilder.addColumn(fieldName);
		});
		CsvSchema csvSchema = csvSchemaBuilder.build().withHeader().withColumnSeparator(separator);

		CsvMapper csvMapper = new CsvMapper();
		String csvString = csvMapper.writerFor(JsonNode.class).with(csvSchema).writeValueAsString(jsonTree);
		return csvString;
	}

	public static String toJSONString(String csvString) throws Exception {
		try {
			CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true)
					.setColumnSeparator(CommonConstants.CSV_COLUMN_SEPARATOR).build().withoutQuoteChar();
			CsvMapper csvMapper = new CsvMapper();
			csvMapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
			
			List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(csvString).readAll();
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
		} catch (Exception e) {
			System.out.println("csvString:" + csvString);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(String csvString, Class<T> clazz) throws Exception {
		try {
			CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true)
					.setColumnSeparator(CommonConstants.CSV_COLUMN_SEPARATOR).build();
	
			CsvMapper csvMapper = new CsvMapper();
			csvMapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
			return (List<T>) csvMapper.readerFor(clazz).with(csvSchema).readValues(csvString).readAll();
		} catch (Exception e) {
			System.out.println("csvString:" + csvString);
			throw e;
		}
	}
	
	public static String fixMissingClosingQuote(String csvRecord, char columnSeparator) {
		if(!StringUtils.contains(csvRecord, CommonConstants.SINGLE_QUOTE)) {
			return csvRecord;
		}
		String[] columns = StringUtils.split(csvRecord, columnSeparator);
		
		for(int i=0; i< columns.length; i++) {
			if(StringUtils.startsWith(columns[i], CommonConstants.SINGLE_QUOTE)) {
				columns[i] = StringUtils.appendIfMissing(columns[i], CommonConstants.SINGLE_QUOTE);
			}
		}
		return StringUtils.join(columns, columnSeparator);
	}
}
