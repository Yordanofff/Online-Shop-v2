package com.project.Onlineshop.Entity.Products;

import com.project.Onlineshop.Entity.ProductHelpers.Color;
import com.project.Onlineshop.Entity.ProductHelpers.Material;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Others extends Product{
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;
}
