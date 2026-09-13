package com.example.speechtotext.dto;


//Same structure from YAML specification
public record Stats (long inputTokens, long outputTokens) {

    public Stats(long inputTokens, long outputTokens) {
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
    }

    public long inputTokens() {
        return inputTokens;
    }

    public long outputTokens() {
        return outputTokens;
    }
}
