package com.ankitsaahariya.Exception;

public class AccountDisabledException extends RuntimeException{
    public AccountDisabledException(String message){
        super(message);
    }
}
