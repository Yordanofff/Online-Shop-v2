package com.project.Onlineshop.Dto.Request;

import com.project.Onlineshop.Entity.ProductHelpers.Brand;
import com.project.Onlineshop.Entity.ProductHelpers.Color;
import com.project.Onlineshop.Entity.ProductHelpers.Material;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {
    @Size(min = 2)
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageLocation;  // TODO: rename to imageFileName?

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
    private boolean isOutdoor;

    public boolean getIsBiodegradable(){
        return this.isBiodegradable;
    }
    public boolean getIsReusable(){
        return this.isReusable;
    }
    public boolean getIsNonSlip(){
        return this.isNonSlip;
    }
    public boolean getIsOutdoor(){
        return this.isOutdoor;
    }
}

