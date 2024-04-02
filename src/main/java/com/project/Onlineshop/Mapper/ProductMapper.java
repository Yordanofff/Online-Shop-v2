package com.project.Onlineshop.Mapper;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Dto.Response.ProductResponseDto;
import com.project.Onlineshop.Entity.Products.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public Product toEntity(ProductRequestDto productRequestDto){
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        product.setImageLocation(productRequestDto.getImageLocation());
        return product;
    }

    public ProductResponseDto dto (Product product) {
        ProductResponseDto productResponseDto =new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setQuantity(product.getQuantity());
        productResponseDto.setImageLocation(product.getImageLocation());
        return productResponseDto;
    }


}
