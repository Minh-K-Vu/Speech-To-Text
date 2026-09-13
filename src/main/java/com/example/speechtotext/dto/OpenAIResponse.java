package com.example.speechtotext.dto;

//Same structed as json output of OpenAI
public record OpenAIResponse(String text, Usage usage){
    public record Usage(
            long input_tokens,
            long output_tokens
    ) {
    }
}
