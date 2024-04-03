package com.project.Onlineshop.Mapper;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Dto.Response.ProductResponseDto;
import com.project.Onlineshop.Entity.Products.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public Product toEntity(ProductRequestDto productRequestDto) {
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        product.setImageLocation(productRequestDto.getImageLocation());
        return product;
    }

    public ProductResponseDto dto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setQuantity(product.getQuantity());
        productResponseDto.setImageLocation(product.getImageLocation());
        return productResponseDto;
    }

    public Food toFood(ProductRequestDto productRequestDto) {
        Food food = new Food();
        setProductProperties(food, productRequestDto);
        food.setExpiryDate(productRequestDto.getExpiryDate());
        return food;
    }

    public Drink toDrink(ProductRequestDto productRequestDto) {
        Drink drink = new Drink();
        setProductProperties(drink, productRequestDto);
        drink.setBestBefore(productRequestDto.getBestBefore());
        return drink;
    }

    public Accessories toAccessories(ProductRequestDto productRequestDto) {
        Accessories accessories = new Accessories();
        setProductProperties(accessories, productRequestDto);
        accessories.setColor(productRequestDto.getColor());
        accessories.setBrand(productRequestDto.getBrand());
        return accessories;
    }

    public Sanitary toSanitary(ProductRequestDto productRequestDto){
        Sanitary sanitary = new Sanitary();
        setProductProperties(sanitary, productRequestDto);
        sanitary.setBiodegradable(productRequestDto.getIsBiodegradable());
        sanitary.setReusable(productRequestDto.getIsReusable());
        sanitary.setMaterial(productRequestDto.getMaterial());
        return sanitary;
    }

    public Railing toRailing(ProductRequestDto productRequestDto){
        Railing railing = new Railing();
        setProductProperties(railing, productRequestDto);
        railing.setOutdoor(productRequestDto.getIsOutdoor());
        railing.setMaterial(productRequestDto.getMaterial());
        railing.setColor(productRequestDto.getColor());
        railing.setBrand(productRequestDto.getBrand());
        railing.setNonSlip(productRequestDto.getIsNonSlip());
        return railing;
    }

    public Decoration toDecoration(ProductRequestDto productRequestDto){
        Decoration decoration = new Decoration();
        setProductProperties(decoration, productRequestDto);
        decoration.setMaterial(productRequestDto.getMaterial());
        decoration.setBrand(productRequestDto.getBrand());
        return decoration;
    }

    public Others toOthers(ProductRequestDto productRequestDto){
        Others others = new Others();
        setProductProperties(others, productRequestDto);
        others.setMaterial(productRequestDto.getMaterial());
        others.setColor(productRequestDto.getColor());
        return others;
    }

    private void setProductProperties(Product product, ProductRequestDto productRequestDto) {
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        product.setImageLocation(productRequestDto.getImageLocation());
    }
}
