package com.project.Onlineshop.Exceptions;

public class UsernameInUseException extends RuntimeException{
    public UsernameInUseException(String message) {
        super(message);
    }
}
