package com.symptomcheck.reviewservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }

    public ReviewAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}