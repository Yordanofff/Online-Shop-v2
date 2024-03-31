package com.project.Onlineshop.Entity.Products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "drinks")
@Data
@NoArgsConstructor
public class Drink extends Product {

    @Column(name = "best_before")
    private LocalDate bestBefore;

    public Drink(String name, BigDecimal price, int quantity, LocalDate bestBefore) {
        super(name, price, quantity);
        this.bestBefore = bestBefore;
    }
}
