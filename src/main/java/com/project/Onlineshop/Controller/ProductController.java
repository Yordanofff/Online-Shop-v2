package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Repository.BrandRepository;
import com.project.Onlineshop.Repository.ColorRepository;
import com.project.Onlineshop.Repository.MaterialRepository;
import com.project.Onlineshop.Repository.ProductRepository;
import com.project.Onlineshop.Service.ImageService;
import com.project.Onlineshop.Service.Implementation.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductServiceImpl productService;
    private final ImageService imageService;
    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;

    @GetMapping("/show/{id}")
    public String showSingleId(Model model, @PathVariable("id") Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            model.addAttribute("product", product);
            return "product_view";
        } else {
            return "404_page_not_found";
        }
    }

    @PostMapping("/add_to_basket")
    public String addToBasket(Model model, @RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        // TODO: logic here
        //  addAttribute - successfully added x items
        //  add button - viewBasket ?
        System.out.println("Adding " + quantity + " items from " + productRepository.findById(productId).get());
        // Adding 5 items from Drink(bestBefore=2024-04-01)
        return "redirect:/products/show/" + productId;
    }

    @GetMapping("/show")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products_all";
    }

    @GetMapping("/filter")
    public String sortProducts(@RequestParam String category, Model model) {
        if (Objects.equals(category, "PRODUCT")) {
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("category", category);
        } else {
            Class<? extends Product> productClass = productService.getProductClass(category);
            if (productClass != null) {
                model.addAttribute("products", productRepository.getAllByEntityType(productClass));
                model.addAttribute("category", category);
            } else {
                throw new NullPointerException("The product category was not found!");
            }
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
        model.addAttribute("productRequestDto", new ProductRequestDto());
        model.addAttribute("productType", productType);

        //       TODO - the ifs below need to be better looking + validations should be added

        if (productType.equalsIgnoreCase("Sanitary") || productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Decoration") || productType.equalsIgnoreCase("Others")) {
            model.addAttribute("materials", materialRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Accessory")) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Decoration")) {
            model.addAttribute("brands", brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Others")) {
            model.addAttribute("colors", colorRepository.findAll());
        }
        return "product_add"; // Return the view to render the form
    }

// TODO - do we need this?
//    private Class<? extends Product> getProductClass(String category) {
//        // TODO - can we use the Enum instead of this?
//        switch (category) {
//            case "ALL":
//                return Product.class;
//            case "FOOD":
//                return Food.class;
//            case "DRINK":
//                return Drink.class;
//            case "SANITARY":
//                return Sanitary.class;
//            case "RAILING":
//                return Railing.class;
//            case "ACCESSORIES":
//                return Accessories.class;
//            case "DECORATION":
//                return Decoration.class;
//            case "OTHERS":
//                return Others.class;
//
//            default:
//                return Others.class;
//        }
//    }

    @PostMapping("/add")
    public String saveProduct(@RequestParam("productType") String productType,
                              @Valid @ModelAttribute ProductRequestDto productRequestDto,
                              BindingResult bindingResult,
                              Model model) {
        return productService.saveProduct(productType, productRequestDto, bindingResult, model);
    }


    // UPLOADING IMAGE TEST ------
    @GetMapping("/upload_image")
    public String displayUploadForm() {
        return "upload_test";
    }

//    @PostMapping("/upload")
//    public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
//        return imageService.uploadImage(model, file);
//    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }

}