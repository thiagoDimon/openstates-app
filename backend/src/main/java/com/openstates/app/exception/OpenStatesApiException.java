package com.openstates.app.exception;

public class OpenStatesApiException extends RuntimeException {

    public OpenStatesApiException(String message) {
        super(message);
    }

    public OpenStatesApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
