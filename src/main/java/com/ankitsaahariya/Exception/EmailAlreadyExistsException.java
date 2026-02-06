package com.ankitsaahariya.Exception;

public class EmailAlreadyExistsException extends  RuntimeException{
    public   EmailAlreadyExistsException(String message){
        super(message);
    }
}
