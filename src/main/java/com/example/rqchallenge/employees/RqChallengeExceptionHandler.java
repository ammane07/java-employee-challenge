package com.example.rqchallenge.employees;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class RqChallengeExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(RqChallengeExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler
            (value = { WebClientResponseException.TooManyRequests.class })
    protected ResponseEntity<Object> handleTooManyRequestErrorHandler(RuntimeException ex, WebRequest request) {
        final String message = Optional.of(ex.getMessage()).orElse(ex.getClass().getSimpleName());

        LOGGER.error("TooManyRequests exception. Error message : " + message);

        Map<String, Object> exceptionDetails = getExceptionDetails(message);
        return new ResponseEntity<>(exceptionDetails, HttpStatus.TOO_MANY_REQUESTS);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
            (value = { WebClientResponseException.InternalServerError.class })
    protected ResponseEntity<Object> handleServerErrorHandler(RuntimeException ex, WebRequest request) {
        final String message = Optional.of(ex.getMessage()).orElse(ex.getClass().getSimpleName());

        LOGGER.error("InternalServerError exception. Error message : " + message);
        Map<String, Object> exceptionDetails = getExceptionDetails(message);
        return new ResponseEntity<>(exceptionDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
            (value = { Exception.class })
    protected ResponseEntity<Object> genericExceptionHandler (Exception ex, WebRequest request) {
        final String message = Optional.of(ex.getMessage()).orElse(ex.getClass().getSimpleName());

        LOGGER.error("Exception occurred. Error message : " + message);

        Map<String, Object> exceptionDetails = getExceptionDetails(message);
        return new ResponseEntity<>(exceptionDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> getExceptionDetails(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        return body;
    }
}
