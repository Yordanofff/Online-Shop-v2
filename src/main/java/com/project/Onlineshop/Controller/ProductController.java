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

    @GetMapping("/show")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products_all";
    }

    @GetMapping("/filter")
    public String filterProducts(@RequestParam String category, Model model) {
        return productService.filterProductsByChosenCategory(category, model);
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
    public String editProduct(@ModelAttribute @Valid Product product, Model model, BindingResult bindingResult) {
        return productService.saveEditedProduct(product, model);
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

    @GetMapping("/searchByPrice")
    public String searchProductsByPriceForm(Model model) {
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        List<Product> products = (List<Product>) productRepository.findAll();
        for (Product p : products) {
            BigDecimal price = p.getPrice();
            if (minPrice == null || price.compareTo(minPrice) < 0) {
                minPrice = price;
            }
            if (maxPrice == null || price.compareTo(maxPrice) > 0) {
                maxPrice = price;
            }
        }
        model.addAttribute("minPrice", minPrice.toString());
        model.addAttribute("maxPrice", maxPrice.toString());
        return "search_by_price";
    }

    @PostMapping("/searchByPrice")
    public String showProductsByPrice(@RequestParam("minPrice") String minPrice,
                                      @RequestParam("maxPrice") String maxPrice,
                                      @RequestParam(name = "minPriceChanged", defaultValue = "false") boolean minPriceChanged,
                                      @RequestParam(name = "maxPriceChanged", defaultValue = "false") boolean maxPriceChanged,
                                      RedirectAttributes redirectAttributes,
                                      Model model){
        if(!minPriceChanged || !maxPriceChanged){
            //TODO - add the message below to the form ?
            redirectAttributes.addFlashAttribute("error","You must choose both min and max values!");
            model.addAttribute("minPrice", minPrice.toString());
            model.addAttribute("maxPrice", maxPrice.toString());
            return "redirect:/products/searchByPrice";
        }
        BigDecimal minPriceDecimal = new BigDecimal(minPrice);
        BigDecimal maxPriceDecimal = new BigDecimal(maxPrice);
        if(minPriceDecimal.compareTo(maxPriceDecimal) > 0){
            redirectAttributes.addFlashAttribute("error","The price selected as the minimum should be smaller than the maximum!");
            return "redirect:/products/searchByPrice";
        }
        model.addAttribute("products", productService.findAllProductsBetweenTwoPrices(minPriceDecimal, maxPriceDecimal));
        model.addAttribute("search_by_price_results", "(Showing products having a price between "+minPriceDecimal+" лв. and "+maxPriceDecimal+" лв.)");
        return "products_all";
    }
}

