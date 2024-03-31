package com.project.Onlineshop.Exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException (String message) {
        super(message);
    }
}
