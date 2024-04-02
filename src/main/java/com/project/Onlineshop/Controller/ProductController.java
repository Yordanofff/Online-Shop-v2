package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Repository.ProductRepository;
import com.project.Onlineshop.Service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ImageService imageService;


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
        // TODO - can we use the Enum instead of this?
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

    // UPLOADING IMAGE TEST ------
    @GetMapping("/uploadimage") public String displayUploadForm() {
        return "upload_test";
    }

    @PostMapping("/upload") public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
        return imageService.uploadImage(model, file);
    }


}
