package com.fpt.sba301.slot22.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String code, String message, Instant timestamp, Map<String, String> fieldErrors) {
    public ApiError(String code, String message) {
        this(code, message, Instant.now(), null);
    }
}
