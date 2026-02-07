package com.ankitsaahariya.Exception;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message){
        super(message);
    }
}
