package com.ankitsaahariya.Exception;

public class PasswordResetNotVerified extends RuntimeException{
    public PasswordResetNotVerified(String message){
        super(message);
    }
}
