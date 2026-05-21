package com.openstates.app.exception;

public class RateLimitException extends OpenStatesApiException {

    public RateLimitException(String message) {
        super(message);
    }
}
