package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Repository.ProductRepository;
import com.project.Onlineshop.Service.Implementation.ProductServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    @Autowired
    private ProductServiceImpl productService;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/show")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products_all";
    }

    @GetMapping("/filter")
    public String sortProducts(@RequestParam String category, Model model) {
        Class<? extends Product> productClass = productService.getProductClass(category);
        if (productClass != null) {
            model.addAttribute("products", productRepository.getAllByEntityType(productClass));
            model.addAttribute("category", category);
        } else {
            throw new NullPointerException("The product category was not found!");
        }
        return "products_all";
    }

    @GetMapping("/edit")
    public String editProductForm(@RequestParam Long id, Model model) {
        if (productRepository.findById(id).isPresent()) {
            model.addAttribute("product", productRepository.findById(id).get());
            return "product_edit";
        }
        throw new NullPointerException("Product with this ID was not found");
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product) {
        Optional<Product> optionalProduct = productRepository.findById(product.getId());
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            productRepository.save(existingProduct);
            return "redirect:/products/show";
        } else {
            throw new IllegalArgumentException("Product with this ID was not found");
        }
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) {
        productRepository.deleteById(id);
        return "redirect:/products/show";
    }

    @GetMapping("/add")
    public String addNewProduct(@RequestParam("productType") String productType, Model model) {
        switch (productType) {
            case "Drink":
                Drink drink = new Drink();
                model.addAttribute("product", drink);
                model.addAttribute("product_type", drink.getClass().getSimpleName());
                break;
            case "Food":
                Food food = new Food();
                model.addAttribute("product", food);
                model.addAttribute("product_type", food.getClass().getSimpleName());
                break;
            case "Sanitary":
                Sanitary sanitary = new Sanitary();
                model.addAttribute("product", sanitary);
                model.addAttribute("product_type", sanitary.getClass().getSimpleName());
                break;
            case "Railing":
                Railing railing = new Railing();
                model.addAttribute("product", railing);
                model.addAttribute("product_type", railing.getClass().getSimpleName());
                break;
            case "Accessory":
                Accessories accessories = new Accessories();
                model.addAttribute("product", accessories);
                model.addAttribute("product_type", accessories.getClass().getSimpleName());
                break;
            case "Decoration":
                Decoration decoration = new Decoration();
                model.addAttribute("product", decoration);
                model.addAttribute("product_type", decoration.getClass().getSimpleName());
                break;
            case "Others":
                Others others = new Others();
                model.addAttribute("product", others);
                model.addAttribute("product_type", others.getClass().getSimpleName());
                break;
            default:
                return "error_page";
        }
        return "product_add";
    }


//    @PostMapping("/add")
//    public <T extends Product> String saveProduct(@ModelAttribute @Valid T product, @RequestParam("productType") String productType, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("product", product);
//            model.addAttribute("product_type", productType);
//            return "product_add";
//        }
//        productRepository.save(product);
//        return "redirect:/index";
//    }


}
