package com.project.Onlineshop.Entity;

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
public class Accessories extends Product {

    @Column
    private Color color;

    public Accessories(String name, BigDecimal price, int quantity, Category category, Color color) {
        super(name, price, quantity, category);
        this.color = color;
    }
}