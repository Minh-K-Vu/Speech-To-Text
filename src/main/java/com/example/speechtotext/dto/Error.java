package com.example.speechtotext.dto;

public record Error(String timestamp, int status, String error, String message, String path
) {

}
