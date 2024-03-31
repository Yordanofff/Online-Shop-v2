package com.project.Onlineshop.Exceptions;

public class PasswordsNotMatchingException extends RuntimeException{
    public PasswordsNotMatchingException(String message) {
        super(message);
    }
}
