package com.project.Onlineshop.Exceptions;

public class PhoneInUseException extends RuntimeException{
    public PhoneInUseException(String message) {
        super(message);
    }
}
