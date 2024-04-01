package com.project.Onlineshop.Entity;

import com.project.Onlineshop.Entity.Products.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
}
