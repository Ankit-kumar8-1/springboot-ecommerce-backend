package com.ankitsaahariya.Exception;

public class EmailSendFailedException extends RuntimeException{
    public EmailSendFailedException(String message){
        super(message);
    }
}
