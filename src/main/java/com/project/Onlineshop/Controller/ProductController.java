package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/show")
    public String showAllProducts(Model model){
        model.addAttribute("products", productRepository.findAll());
        return "products_all";
    }

    @GetMapping("/filter")
    public String sortProducts(@RequestParam String category, Model model){
        Class<? extends Product> productClass = getProductClass(category);
        if (productClass != null) {
            model.addAttribute("products", productRepository.getAllByEntityType(productClass));
            model.addAttribute("category", category);
        } else {
            throw new NullPointerException("The product category was not found!");
        }
        return "products_all";
    }

    private Class<? extends Product> getProductClass(String category) {
        switch (category) {
            case "FOOD":
                return Food.class;
            case "DRINK":
                return Drink.class;
            case "SANITARY":
                return Sanitary.class;
            case "RAILING":
                return Railing.class;
            case "ACCESSORIES":
                return Accessories.class;
            case "DECORATION":
                return Decoration.class;
            case "OTHERS":
                return Others.class;
            default:
                return null; // Handle unknown category
        }
    }

}
