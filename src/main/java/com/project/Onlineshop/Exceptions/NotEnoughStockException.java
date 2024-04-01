package com.project.Onlineshop.Exceptions;

import com.project.Onlineshop.Entity.Products.Product;
import lombok.Getter;

@Getter
public class NotEnoughStockException extends RuntimeException{

    private final Product product;
    private final int availableQuantity;

    public NotEnoughStockException(Product product, int availableQuantity) {
        super("Not enough stock of product: \"" + product + "\" in stock");
        this.product = product;
        this.availableQuantity = availableQuantity;
    }
}
