package com.ankitsaahariya.Exception;

public class VerificationTokenExpiredException extends RuntimeException{
    public  VerificationTokenExpiredException(String message){
        super(message);
    }

}
