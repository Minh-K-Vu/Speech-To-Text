package com.example.speechtotext.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.speechtotext.dto.Stats;
import com.example.speechtotext.service.TokenTracker;

//Set up get request to global/stats

@RestController
@RequestMapping("/api/v1/global")
public class StatsController {

    private TokenTracker tokenTracker;

    public StatsController(
            TokenTracker tokenTracker) {

        this.tokenTracker = tokenTracker;
    }
// get request to /api/v1/global/stats

    @GetMapping("/stats")
    public ResponseEntity<Stats> getStats() {

        return ResponseEntity.ok(
                tokenTracker.getStats()
        );
    }
}
