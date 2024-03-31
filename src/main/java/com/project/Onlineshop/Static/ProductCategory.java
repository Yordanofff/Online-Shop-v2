package com.project.Onlineshop.Static;

public enum ProductCategory {

    FOOD(true),
    DRINK(true),
    SANITARY(false),
    RAILING(false),
    ACCESSORIES(false),
    DECORATION(false),
    OTHERS(false);

    private final boolean expirable;

    ProductCategory(boolean expirable) {
        this.expirable = expirable;
    }

    public boolean isExpirable() {
        return expirable;
    }
}

