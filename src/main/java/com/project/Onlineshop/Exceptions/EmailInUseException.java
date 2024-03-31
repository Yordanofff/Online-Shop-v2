package com.project.Onlineshop.Exceptions;

public class EmailInUseException extends RuntimeException{
    public EmailInUseException(String message) {
        super(message);
    }
}
