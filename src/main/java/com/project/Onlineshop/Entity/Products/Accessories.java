package com.project.Onlineshop.Entity.Products;

import com.project.Onlineshop.Entity.ProductHelpers.Brand;
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

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Accessories(String name, BigDecimal price, int quantity, Color color, Brand brand) {
        super(name, price, quantity);
        this.color = color;
        this.brand = brand;
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