package com.example.speechtotext.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.speechtotext.dto.UptimeResponse;
import com.example.speechtotext.service.ServerLifeTracker;
import com.example.speechtotext.service.ShutdownService;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.speechtotext.dto.Error;
import com.example.speechtotext.dto.Shutdown;

@RestController
@RequestMapping("/api/v1/admin")
public class Admin {
	private ServerLifeTracker serverLifeTracker;
	private ShutdownService shutdownService;
	public Admin(ServerLifeTracker serverLifeTracker, ShutdownService shutdownService) {
		this.serverLifeTracker = serverLifeTracker;
		this.shutdownService = shutdownService;
	}
	//Get request for /api/v1/admin/uptime
	@GetMapping("/uptime")
	public ResponseEntity<UptimeResponse> getUptime() {
		//get response from getUptime()
		UptimeResponse response =
                serverLifeTracker.getUptime();
		//Reponse
        return ResponseEntity.ok(response);
	}
	//Post request for /api/v1/admin/shutdown
	@PostMapping("/shutdown")
	public ResponseEntity<?> shutdown() {
		//Use shutdown service to request shutdown.
	    boolean accepted =
	            shutdownService.requestShutdown();
	    //Check if a shutdown is already in progress
	    if (!accepted) {
	        Error error = new Error(Instant.now().toString(), 409,
	                        "Conflict",
	                        "Shutdown already in progress",
	                        "/api/v1/admin/shutdown"
	                );
	        return ResponseEntity
	                .status(HttpStatus.CONFLICT)
	                .body(error);
	    }
	    // shutdown response
	    Shutdown response =
	            new Shutdown(
	                    "Graceful shutdown requested."
	            );
	    return ResponseEntity
	            .status(HttpStatus.ACCEPTED)
	            .body(response);
	}
}
