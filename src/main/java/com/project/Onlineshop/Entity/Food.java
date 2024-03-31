package com.project.Onlineshop.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "foods")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private LocalDate expiryDate;

    @OneToOne
    @JoinColumn(name = "product_information_id")
    private ProductInformation productInformation;

    public Food(LocalDate expiryDate, ProductInformation productInformation) {
        this.expiryDate = expiryDate;
        this.productInformation = productInformation;
    }
}