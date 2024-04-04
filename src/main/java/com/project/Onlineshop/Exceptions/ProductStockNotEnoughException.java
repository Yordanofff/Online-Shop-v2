package com.project.Onlineshop.Exceptions;

public class ProductStockNotEnoughException extends RuntimeException{
    public ProductStockNotEnoughException(String message) {
        super(message);
    }
}
