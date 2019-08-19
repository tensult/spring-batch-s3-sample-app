package com.tensult.service;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@Service
public class APIService {

	@Value("${server.port}")
	private String serverPort;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	public String getAPIResponse(String jsonBodyasString) throws JSONException {
			
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
     	String requestUrl = String.format("http://localhost:%s/api", serverPort);

		HttpEntity<String> entity = new HttpEntity<String>(jsonBodyasString, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, entity, String.class);
		return response.getBody();

	}

}

