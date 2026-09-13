package com.example.speechtotext.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;


@Service
public class TranscriptionService {
	
	private RestClient restClient;
	
	public TranscriptionService(
			//Let Spring construct RestClient
            RestClient.Builder restClientBuilder,
            //Read openai.api-key and store in apiKey
            @Value("${openai.api-key}") String apiKey,
            //Read openai.base-url and store in baseUrl
            @Value("${openai.base-url}") String baseUrl) {
		
		//using restClientBuilder we set:
		//baseUrl = https://api.openai.com
		//all requests will have header:
		//Authorization, Bearer + apiKey
		
        restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();
    }

	
	// Get a file as input and return a string
	public String transcribe(MultipartFile file) {
		return "Filename: " + file.getOriginalFilename();
	}
}
