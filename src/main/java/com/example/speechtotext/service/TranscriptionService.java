package com.example.speechtotext.service;
import com.example.speechtotext.service.TokenTracker;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//Replace RestClient with WebClient to allow asynchronous HTTP calls.
import org.springframework.web.reactive.function.client.WebClient;
//Use Mono for asynchronous
import reactor.core.publisher.Mono;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.speechtotext.dto.OpenAIResponse;



@Service
public class TranscriptionService {
	
	private WebClient webClient;
	private TokenTracker tokenTracker;
	
	public TranscriptionService(
			//Let Spring construct RestClient
            WebClient.Builder restClientBuilder,
            //Read openai.api-key and store in apiKey
            @Value("${openai.api-key}") String apiKey,
            //Read openai.base-url and store in baseUrl
            @Value("${openai.base-url}") String baseUrl,
            TokenTracker tokenTracker) {
		
		this.tokenTracker = tokenTracker;
		
		//using restClientBuilder we set:
		//baseUrl = https://api.openai.com
		//all requests will have header:
		//Authorization, Bearer + apiKey
		
        webClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();
       
    }
	
	
	// Get a file as input, post request to OpenAi and return OpenAi response
	public Mono<String> transcribe(MultipartFile file) {
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
	        return webClient.post()
                    .uri("/v1/audio/transcriptions") //URL request
                    .contentType(
                            MediaType.MULTIPART_FORM_DATA //set content being sent to multipart form data which support multivaluemap
                    )
                    //body request
                    .bodyValue(requestBody)
                    .retrieve() // get the response
                    .bodyToMono(OpenAIResponse.class)
                    .switchIfEmpty( //if body is empty return error
                            Mono.error(
                                    new IllegalStateException(
                                            "OpenAI did not respond"
                                    )
                            )
                    )
                    .map(response -> {
                        // Add usage
                        if (response.usage() != null) {

                            tokenTracker.addUsage(
                                    response.usage().input_tokens(),
                                    response.usage().output_tokens()
                            );
                        }
                        return response.text();
                    });
		} catch (IOException error) {
			// Change from string to Mono for error handling
	        return Mono.error(
	                new IllegalStateException(
	                        "There was a problem reading the uploaded file",
	                        error
	                )
	        ); 
		}
	}
}
