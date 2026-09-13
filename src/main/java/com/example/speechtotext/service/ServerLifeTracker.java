package com.example.speechtotext.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.speechtotext.dto.UptimeResponse;

//Create api route to track the server uptime
@Service
public class ServerLifeTracker {
	private Instant serverStart;
	
	public ServerLifeTracker() {
		//Initialize server start time;
		this.serverStart = Instant.now();
	}
	
	public UptimeResponse getUptime() {
		
		//Get current time
		Instant timeNow = Instant.now();
		//Calculate up time
		Duration uptime = Duration.between(serverStart, timeNow);
		//Convert up time to seconds 
		double uptimeSeconds = uptime.getSeconds() + + (uptime.getNano() / 1_000_000_000.0);
		
		return new UptimeResponse(serverStart.toString(), timeNow.toString(), uptimeSeconds);
				
	}
}
