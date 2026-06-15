package com.snaplink.api.exception;

public class EmailAlreadyInUseException extends RuntimeException{

    public EmailAlreadyInUseException(String message){
        super(message);
    }
}
