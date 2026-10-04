package com.example.assignment.order.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error
) {
}
