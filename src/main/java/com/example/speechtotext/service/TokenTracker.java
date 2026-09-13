package com.example.speechtotext.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.speechtotext.dto.Stats;


@Service
public class TokenTracker {
	// Start at zero 
	// Using atomic long helps concurrency when multiple is running
    private AtomicLong inputTokens =
            new AtomicLong(0);

    private AtomicLong outputTokens =
            new AtomicLong(0);


    //Add usage
    public void addUsage(
            long input,
            long output) {

        inputTokens.addAndGet(input);
        outputTokens.addAndGet(output);
    }


    //Use stats to get the global usage
    public Stats getStats() {

        return new Stats(
                inputTokens.get(),
                outputTokens.get()
        );
    }
}
