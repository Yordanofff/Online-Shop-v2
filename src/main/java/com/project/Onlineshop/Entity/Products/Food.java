package com.project.Onlineshop.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food extends Product {

    @Column
    private LocalDate expiryDate;

    public Food(String name, BigDecimal price, int quantity, LocalDate expiryDate) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
    }
}