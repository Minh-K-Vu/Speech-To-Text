package com.example.speechtotext.controller;

import com.example.speechtotext.service.TranscriptionService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
//Base URL
@RequestMapping("/api/speechtotext")
public class TranscriptionController {
	private TranscriptionService transcriptionService;
	// constructor dependency injection
	public TranscriptionController(TranscriptionService transcriptionService) {
		this.transcriptionService = transcriptionService;
	}
	//Get request to check the health of controller 
    @GetMapping("/health")
    public String health() {
        return "Active";
    }
    //Post request to handle transcription
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> transcribe(@RequestPart("file") MultipartFile file) {
    	System.out.println("Transcribe endpoint called");

        System.out.println(
            "Filename: " + file.getOriginalFilename()
        );

        System.out.println(
            "Content type: " + file.getContentType()
        );

        System.out.println(
            "File size: " + file.getSize()
        );
        //Pass file to transciptionServie
        String transcription = transcriptionService.transcribe(file);
        
        return ResponseEntity.ok(
            transcription
        );
        
    }
    		
}
