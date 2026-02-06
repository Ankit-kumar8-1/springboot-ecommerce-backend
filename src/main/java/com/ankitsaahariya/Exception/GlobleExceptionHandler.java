package com.ankitsaahariya.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobleExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobleExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
        log.warn("EmailAlreadyExistsException: {}", ex.getMessage(),ex);
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    private ResponseEntity<Map<String,Object>> buildResponse(HttpStatus status, String message){
        Map<String,Object> body = Map.of("timestamp", Instant.now(),"error",message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EmailSendFailedException.class)
    public ResponseEntity<Map<String,Object>> EmailNotVerified(EmailSendFailedException ex){
        log.warn("EmailNotVerifiedException: {}", ex.getMessage(),ex);
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }
}
