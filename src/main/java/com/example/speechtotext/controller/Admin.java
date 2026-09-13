package com.example.speechtotext.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.speechtotext.dto.UptimeResponse;
import com.example.speechtotext.service.ServerLifeTracker;

@RestController
@RequestMapping("/api/v1/admin")
public class Admin {
	private ServerLifeTracker serverLifeTracker;
	public Admin(ServerLifeTracker serverLifeTracker) {
		this.serverLifeTracker = serverLifeTracker;
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
}
