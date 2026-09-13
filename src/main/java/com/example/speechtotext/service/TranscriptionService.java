package com.example.speechtotext.service;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.speechtotext.dto.OpenAIResponse;


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

	
	// Get a file as input, post request to OpenAi and return OpenAi response
	public String transcribe(MultipartFile file) {
		try {
			//Convert file into a resource. RestClient can work with resource.
			ByteArrayResource audioResource =
	                new ByteArrayResource(file.getBytes()) { //get raw bytes of file
	            @Override
	            public String getFilename() {
	                String originalFilename =
	                        file.getOriginalFilename();
	                if (originalFilename != null) {
	                    return originalFilename;
	                }
	                return "recording.webm";
	            }
	        };
	        //Create a map that stores data requests needed to send to OpenAi
	        //MultiValueMap supports resource so we can send the file.
	        //OpenAi needs to receive a file and the model.
	        MultiValueMap<String, Object> requestBody =
	                new LinkedMultiValueMap<>();
	        //File
	        requestBody.add(
	                "file",
	                audioResource
	        );
	        //AI model
	        requestBody.add(
	                "model",
	                "gpt-4o-mini-transcribe"
	        );
	        //Post request to send to OpenAi
	        OpenAIResponse response = restClient.post()
                    .uri("/v1/audio/transcriptions") //URL request
                    .contentType(
                            MediaType.MULTIPART_FORM_DATA //set content being sent to multipart form data which support multivaluemap
                    )
                    //body request
                    .body(requestBody)
                    .retrieve() // get the response
                    .body(
                            OpenAIResponse.class
                    );
	        if (response == null) {
	        	return "OpenAi did not response";
	        			 
	        }
	        return response.text();
	        
		} catch (IOException error) {
			return "There was a problem uploading file" + error;
		}
	}
}
