package com.ankitsaahariya.Exception;

public class VerificationTokenStillValidException extends RuntimeException{
    public VerificationTokenStillValidException(String message){
        super(message);
    }
}
