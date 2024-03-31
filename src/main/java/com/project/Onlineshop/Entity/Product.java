package com.project.Onlineshop.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//@MappedSuperclass
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "products")
public abstract class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;

//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;

    protected Product() {
    }

//    public Product(String name, BigDecimal price, int quantity, Category category) {
    public Product(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
//        this.category = category;
    }

}