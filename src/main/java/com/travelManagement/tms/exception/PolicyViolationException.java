package com.travelManagement.tms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This ensures the frontend gets a 400 Bad Request instead of a 500 Server Error
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PolicyViolationException extends RuntimeException {
    public PolicyViolationException(String message) {
        super(message);
    }
}