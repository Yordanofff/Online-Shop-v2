package com.project.Onlineshop.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

//@Table(name = "foods")
//@DiscriminatorValue("food")
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