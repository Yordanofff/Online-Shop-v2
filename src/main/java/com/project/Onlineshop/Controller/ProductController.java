package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderProduct;
import com.project.Onlineshop.Entity.Products.Food;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.ImageService;
import com.project.Onlineshop.Service.Implementation.OrderServiceImpl;
import com.project.Onlineshop.Service.Implementation.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
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
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderServiceImpl orderService;

    @GetMapping("/show/{id}")
    public String showSingleId(Model model, @PathVariable("id") Long id) {
        return productService.showSingleId(model, id);
    }

    @PostMapping("/add_to_basket")
    public String addToBasket(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity,
                              RedirectAttributes redirectAttributes) {
        return productService.addToBasket(productId, quantity, redirectAttributes);
    }

    @GetMapping("/show_basket")
    public String showBasket(Model model, Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Order basketOrder = productService.getBasketOrder(user);
        model.addAttribute("userDetails", userDetails);
        if (basketOrder != null) {
            model.addAttribute("orderedProducts", orderProductRepository.findAllByOrderId(basketOrder.getId()));
            model.addAttribute("totalPrice", calculateOrderPrice(basketOrder.getId()));
        }
        model.addAttribute("noProductsInBasket", "");
        return "basket";
    }

    private BigDecimal calculateOrderPrice(Long orderId) {
        List<OrderProduct> allProducts = orderProductRepository.findAllByOrderId(orderId);
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (OrderProduct op : allProducts) {
            BigDecimal productPrice = op.getProduct().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(op.getQuantity());
            totalPrice = totalPrice.add(productPrice.multiply(quantity));
        }
        return totalPrice;
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
        return productService.addNewProduct(productType, model);
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

    @PostMapping("/save")
    public String saveProduct(@RequestParam("productType") String productType,
                              @Valid @ModelAttribute("productRequestDto") ProductRequestDto productRequestDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        return productService.saveProduct(productType, productRequestDto, bindingResult, redirectAttributes);
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

    @GetMapping("/search")
    public String searchForProduct(@RequestParam String searchString, Model model) {
        if (!searchString.isEmpty()) {
            model.addAttribute("products", productService.searchProducts(searchString));
        }
        model.addAttribute("searchString", searchString);
        return "products_found";
    }

    //TODO - Filtering by expiryDate & Show all products right after does not load the products, just the Foods.
    @GetMapping("/sort")
    public String showSortedProducts(@RequestParam String sortType, Model model) {
        if (sortType.equalsIgnoreCase("byName")) {
            List<Product> products = (List<Product>) productRepository.findAll();
            products.sort(Comparator.comparing(Product::getName));
            model.addAttribute("products", products);
        }
        if (sortType.equalsIgnoreCase("byPrice")) {
            List<Product> products = (List<Product>) productRepository.findAll();
            products.sort(Comparator.comparing(Product::getPrice));
            model.addAttribute("products", products);
        }
        if (sortType.equalsIgnoreCase("byExpiryDate")) {
            List<Food> foods = productRepository.findAllBy();
            foods.sort(Comparator.comparing(Food::getExpiryDate));
            model.addAttribute("products", foods);
        }
        return "products_all";
    }
}
