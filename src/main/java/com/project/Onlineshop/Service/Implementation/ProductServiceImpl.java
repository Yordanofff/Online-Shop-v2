package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Mapper.ProductMapper;
import com.project.Onlineshop.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public Class<? extends Product> getProductClass(String category) {
        return switch (category) {
            case "FOOD" -> Food.class;
            case "DRINK" -> Drink.class;
            case "SANITARY" -> Sanitary.class;
            case "RAILING" -> Railing.class;
            case "ACCESSORIES" -> Accessories.class;
            case "DECORATION" -> Decoration.class;
            case "OTHERS" -> Others.class;
            default -> null; // Handle unknown category
        };
    }

    public String saveProduct(String productType,
                              ProductRequestDto productRequestDto,
                              BindingResult bindingResult,
                              Model model) {


        Product product;
        // TODO - binding results is catching the errors but they are not being displayed.
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", productRequestDto);
            model.addAttribute("product_type", productType);
            return "product_add";
        }

        if (productType.equalsIgnoreCase("food")) {
            product = productMapper.toFood(productRequestDto);
        } else if (productType.equalsIgnoreCase("drink")) {
            product = productMapper.toDrink(productRequestDto);
        } else if (productType.equalsIgnoreCase("accessories")) {
            product = productMapper.toAccessories(productRequestDto);
        } else {
            // TODO - add all categories
            product = new Product();
        }

        productRepository.save(product);
        return "redirect:/products/show";
    }

}
