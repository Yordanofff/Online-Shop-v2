package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Repository.ProductRepository;
import com.project.Onlineshop.Service.ImageService;
import com.project.Onlineshop.Service.Implementation.ProductServiceImpl;
import com.project.Onlineshop.Utility.PriceRange;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductServiceImpl productService;
    private final ImageService imageService;

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
    public String showAllProducts(@RequestParam(required = false) String sortType,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) boolean ascending,
                                  @RequestParam(value = "minPrice", required = false) String minPrice,
                                  @RequestParam(value = "maxPrice", required = false) String maxPrice,
                                  Model model) {
        List<Product> products = productRepository.findByIsDeletedFalse(); // Default - show all (enabled only)

        // Show the min max price range - on the sliders. Will keep the same range for all items for now. Keep it simple.
        BigDecimal minPriceRange = getMinPriceOfSelectedProducts(products);
        BigDecimal maxPriceRange = getMaxPriceOfSelectedProducts(products);
        model.addAttribute("minPriceR", minPriceRange.toString());
        model.addAttribute("maxPriceR", maxPriceRange.toString());

        if (category != null) {
            model.addAttribute("category", category);
            products = productService.getTheProductsToShow(category);  // if category is selected - update the list
        }

        if (minPrice == null) {
            model.addAttribute("minPrice", minPriceRange); // if null - set it to the minimum to show all products.
            minPrice = minPriceRange.toString();
        }
        if (maxPrice == null) {
            model.addAttribute("maxPrice", maxPriceRange);
            maxPrice = maxPriceRange.toString();
        }

        // At this point we always have minPrice and MaxPrice as Strings.

        BigDecimal minPriceDecimalSelected = new BigDecimal(minPrice);
        BigDecimal maxPriceDecimalSelected = new BigDecimal(maxPrice);
        PriceRange priceRange = PriceRange.getMinMaxNumber(minPriceDecimalSelected, maxPriceDecimalSelected);
        BigDecimal actualMinPrice = priceRange.getMin();
        BigDecimal actualMaxPrice = priceRange.getMax();
        products = getAllProductsInPriceRange(actualMinPrice, actualMaxPrice, products);  // apply the new filter

        if (sortType != null) {
            return productService.showSortedProductsBySortType(sortType, ascending, model, products);
        }

        model.addAttribute("products", products); // if no sorting is selected
        return "products_all";
    }

    private List<Product> getAllProductsInPriceRange(BigDecimal min, BigDecimal max, List<Product> products) {
        List<Product> productsInPriceRange = new ArrayList<>();

        if (products != null) {
            for (Product product : products) {
                BigDecimal price = product.getPrice();
                if (price != null && price.compareTo(min) >= 0 && price.compareTo(max) <= 0) {
                    productsInPriceRange.add(product);
                }
            }
        }

        return productsInPriceRange;
    }

    private BigDecimal getMinPriceOfSelectedProducts(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            BigDecimal minPrice = products.getFirst().getPrice();

            for (int i = 1; i < products.size(); i++) {
                BigDecimal currentPrice = products.get(i).getPrice();
                if (currentPrice != null && currentPrice.compareTo(minPrice) < 0) {
                    minPrice = currentPrice;
                }
            }
            return minPrice;
        } else {
            return null;
        }
    }

    public BigDecimal getMaxPriceOfSelectedProducts(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            BigDecimal maxPrice = products.get(0).getPrice();

            for (int i = 1; i < products.size(); i++) {
                BigDecimal currentPrice = products.get(i).getPrice();
                if (currentPrice != null && currentPrice.compareTo(maxPrice) > 0) {
                    maxPrice = currentPrice;
                }
            }
            return maxPrice;
        } else {
            return null;
        }
    }

//    @GetMapping("/show/filter")
//    public String filterProducts(@RequestParam String category, Model model) {
//        return productService.filterProductsByChosenCategory(category, model);
//    }

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
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return "404_page_not_found";
        }
        // Not  deleting the item - just changing the flag.
        Product product = optionalProduct.get();
        // Less DB operations - if someone else just did that.
        if (!product.isDeleted()) {
            product.setDeleted(true);
            productRepository.save(product);
        }
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
        return productService.searchProductByName(searchString, model);
    }

    @GetMapping("/searchByPrice")
    public String searchProductsByPrice(Model model) {
        return productService.showSearchByPriceResults(model);
    }

    @PostMapping("/searchByPrice")
    public String showResultProductsByPrice(@RequestParam("minPrice") String minPrice,
                                            @RequestParam("maxPrice") String maxPrice,
                                            @RequestParam(name = "minPriceChanged", defaultValue = "false") boolean minPriceChanged,
                                            @RequestParam(name = "maxPriceChanged", defaultValue = "false") boolean maxPriceChanged,
                                            RedirectAttributes redirectAttributes,
                                            Model model) {
        return productService.showProductsForChosenPrice(minPrice, maxPrice, minPriceChanged, maxPriceChanged, redirectAttributes, model);
    }

    @GetMapping("/searchByQuantity")
    public String searchProductsByQuantity(Model model) {
        int minQuantity = productRepository.findProductWithLowestQuantity().get().getQuantity();
        int maxQuantity = productRepository.findProductWithHighestQuantity().get().getQuantity();
        model.addAttribute("minQuantity", minQuantity);
        model.addAttribute("maxQuantity", maxQuantity);
        return "search_by_quantity";
    }

    @PostMapping("/searchByQuantity")
    public String showResultProductsByQuantity(@RequestParam(value = "minQuantity", required = false) Integer minQuantity,
                                               @RequestParam(value = "maxQuantity", required = false) Integer maxQuantity,
                                               Model model) {
        if (minQuantity == null) {
            minQuantity = 0;
        }
        if (maxQuantity == null) {
            maxQuantity = Integer.MAX_VALUE;
        }
        return productService.showSearchByQuantityResults(minQuantity, maxQuantity, model);
    }
}

