package com.project.Onlineshop.Exceptions;

public class UserIdDoestExistException extends RuntimeException{
    public UserIdDoestExistException(String message) {
        super(message);
    }
}
