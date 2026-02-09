package com.ankitsaahariya.Exception;

public class WrongOtpException extends RuntimeException{
    public WrongOtpException(String message){
        super(message);
    }
}
