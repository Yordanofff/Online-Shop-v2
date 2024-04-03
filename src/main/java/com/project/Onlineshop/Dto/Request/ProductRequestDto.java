package com.project.Onlineshop.Dto.Request;

import com.project.Onlineshop.Entity.ProductHelpers.Brand;
import com.project.Onlineshop.Entity.ProductHelpers.Color;
import com.project.Onlineshop.Entity.ProductHelpers.Material;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {
    @Size(min = 2)
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageLocation;

//    private String productType; // will be creating different products of this

    // Food
    private LocalDate expiryDate;

    // Drink
    private LocalDate bestBefore;

    private Material material;
    private Brand brand;
    private Color color;

    private boolean isBiodegradable;
    private boolean isReusable;
    private boolean isNonSlip;

}

