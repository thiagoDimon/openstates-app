package com.openstates.app.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.openstates.app.dto.ErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimitException(RateLimitException e) {
        log.warn("Rate limit reached: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponseDTO(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        "OpenStates API rate limit reached. Please try again later.",
                        LocalDateTime.now()));
    }

    @ExceptionHandler(OpenStatesApiException.class)
    public ResponseEntity<ErrorResponseDTO> handleOpenStatesApiException(OpenStatesApiException e) {
        log.error("OpenStates API error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_GATEWAY.value(),
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred.",
                        LocalDateTime.now()));
    }
}
