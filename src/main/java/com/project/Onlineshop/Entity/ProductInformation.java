package com.project.Onlineshop.Entity;

import com.project.Onlineshop.Static.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

@Table(name = "product_information")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



    public ProductInformation(String name, BigDecimal price, int quantity, ProductCategory category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = new Category(category.getId());
    }

    public ProductCategory getCategory() {
        return Arrays.stream(ProductCategory.values()).filter((a) -> a.getId() == this.category.getId()).findFirst().get();
    }
}
