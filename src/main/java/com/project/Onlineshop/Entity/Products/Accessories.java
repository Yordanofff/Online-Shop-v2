package com.project.Onlineshop.Entity.Products;

import com.project.Onlineshop.Entity.ProductHelpers.Color;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
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