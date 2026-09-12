package com.example.speechtotext.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//Base URL
@RequestMapping("/api/texttospeech")
public class TranscriptionController {
    @GetMapping("/health")
    public String health() {
        return "Active";
    }
}
