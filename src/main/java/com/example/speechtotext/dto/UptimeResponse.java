package com.example.speechtotext.dto;

// Json structure for /api/v1/admin/uptime API.
public record UptimeResponse (String utcServerStart, String utcNow, double serverUptimeSeconds) {

}
