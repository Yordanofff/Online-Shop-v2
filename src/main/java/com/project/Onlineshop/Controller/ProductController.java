package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Service.ImageService;
import com.project.Onlineshop.Service.ProductService;
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


@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;

    @GetMapping("/show/{id}")
    public String showSingleId(Model model, @PathVariable("id") Long id) {
        return productService.showSingleId(model, id);
    }

    @PostMapping("/add_to_basket")
    public String addToBasket(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity,
                              RedirectAttributes redirectAttributes) {
        return productService.addToBasket(productId, quantity, redirectAttributes, false);
    }

    @PostMapping("/add_to_basket/from_all_products")
    public String addToBasketFromAllProducts(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        return productService.addToBasket(productId, 1, redirectAttributes, true);
    }

    @GetMapping("/show")
    public String showAllProducts(@RequestParam(required = false) String sortType,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) boolean ascending,
                                  @RequestParam(value = "minPrice", required = false) String minPrice,
                                  @RequestParam(value = "maxPrice", required = false) String maxPrice,
                                  Model model) {
        return productService.showAllProducts(sortType, category, ascending, minPrice, maxPrice, model);
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        return productService.editProductForm(id, model);
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute @Valid Product product, @RequestParam("quantityChange") String quantityChange,
                              Model model, RedirectAttributes redirectAttributes) {
        return productService.saveEditedProduct(product, model, quantityChange, redirectAttributes);
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/undelete")
    public String undeleteProduct(@RequestParam Long id) {
        return productService.undeleteProduct(id);
    }

    @GetMapping("/show/deleted")
    public String showAllDeletedProducts(Model model){
        return productService.showAllDeletedProducts(model);
    }

    @GetMapping("/show/deleted/{id}")
    public String showSingleDeletedId(Model model, @PathVariable("id") Long id){
        return productService.showSingleIdIncludingDeleted(model, id);
    }

    @GetMapping("/add")
    public String addNewProduct(@RequestParam("productType") String productType, Model model) {
        return productService.addNewProduct(productType, model);
    }

    @PostMapping("/save")
    public String saveProduct(@RequestParam("productType") String productType,
                              @Valid @ModelAttribute("productRequestDto") ProductRequestDto productRequestDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        return productService.saveProduct(productType, productRequestDto, bindingResult, redirectAttributes);
    }

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
        return productService.showSearchByPriceForm(model);
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
        return productService.searchProductsByQuantity(model);
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

    // UPLOADING IMAGE TEST ------
    //    @GetMapping("/upload_image")
    //    public String displayUploadForm() {
    //        return "upload_test";
    //    }

    //    @PostMapping("/upload")
    //    public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
    //        return imageService.uploadImage(model, file);
    //    }

    //    @GetMapping("/show/filter")
    //    public String filterProducts(@RequestParam String category, Model model) {
    //        return productService.filterProductsByChosenCategory(category, model);
    //    }
}

