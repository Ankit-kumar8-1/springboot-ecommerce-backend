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

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message
    ) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "error", message
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EmailSendFailedException.class)
    public ResponseEntity<Map<String,Object>> EmailNotVerified(EmailSendFailedException ex){
        log.warn("EmailNotVerifiedException: {}", ex.getMessage(),ex);
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<Map<String,Object>>InvalidVerificationToken(InvalidVerificationTokenException ex){
        log.warn("InvalidVerificationTokenException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<Map<String,Object>>UserAlreadyVerified(UserAlreadyVerifiedException ex){
        log.warn("UserAlreadyVerifiedException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NO_CONTENT,ex.getMessage());
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<Map<String,Object>>VerificationTokenExpired(VerificationTokenExpiredException ex){
        log.warn("VerificationTokenExpiredException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.GONE,ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>>UserNotFound(UserNotFoundException ex){
        log.warn("UserNotFoundException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<Map<String,Object>>  AccountDisabled(AccountDisabledException ex){
        log.warn("AccountDisabledException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.FORBIDDEN,ex.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String,Object>> EmailNotVerified(EmailNotVerifiedException ex){
        log.warn("EmailNotVerifiedException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.FORBIDDEN,ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,Object> >UserNotFound(InvalidCredentialsException ex){
        log.warn("InvalidCredentialsException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }


    @ExceptionHandler(VerificationTokenStillValidException.class)
    public ResponseEntity<Map<String,Object> >VerificationTokenStillValid(VerificationTokenStillValidException ex){
        log.warn("VerificationTokenStillValidException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
    }

    @ExceptionHandler(forgotPasswordRequestAlreadyAccepted.class)
    public ResponseEntity<Map<String,Object> >  forgotPasswordRequestAlreadyAccepted(forgotPasswordRequestAlreadyAccepted ex){
        log.warn("forgotPasswordRequestAlreadyAccepted: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NO_CONTENT,ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFountException.class)
    public ResponseEntity<Map<String,Object> > ResourceNotFoundException(ResourceNotFountException ex){
        log.warn("ResourceNotFoundException: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(PasswordResetNotVerified.class)
    public ResponseEntity<Map<String,Object> > PasswordResetNotVerified(PasswordResetNotVerified ex){
        log.warn("PasswordResetNotVerified: {}",ex.getMessage(),ex);
        return buildResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
    }









}
