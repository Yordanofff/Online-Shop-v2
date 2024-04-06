package com.project.Onlineshop.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.Onlineshop.Entity.Products.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public OrderProduct(Long id, String name, BigDecimal price, int quantity) {
        this.product = Product.builder().id(id).name(name).price(price).build();
        this.quantity = quantity;
    }
}

// 1 ябълка 10
// 1 круши 5
// 1 сливи 2
// 2 ябълка 2