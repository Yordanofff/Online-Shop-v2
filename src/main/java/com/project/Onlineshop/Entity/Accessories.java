package com.project.Onlineshop.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accessories extends Product {

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    public Accessories(String name, BigDecimal price, int quantity, Color color) {
        super(name, price, quantity);
        this.color = color;
    }

    @Override
    public String toString() {
        return "Accessories{" +
                "color=" + color +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}