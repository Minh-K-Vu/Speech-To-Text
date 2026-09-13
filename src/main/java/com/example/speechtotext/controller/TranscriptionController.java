package com.example.speechtotext.controller;
import com.example.speechtotext.service.TranscriptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
//Base URL
@RequestMapping("/api/texttospeech")
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
    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(@RequestParam("file") MultipartFile file) {
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
        transcriptionService.transcribe(file);
        
        return ResponseEntity.ok(
            "Audio recieved" 
        );
        
    }
    		
}
