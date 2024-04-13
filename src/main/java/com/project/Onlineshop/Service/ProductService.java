package com.project.Onlineshop.Service;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Entity.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface ProductService {
    Class<? extends Product> getProductClass(String category);

    List<Product> searchProducts(String keyword);

    String addNewProduct(String productType, Model model);

    String saveProduct(String productType, ProductRequestDto productRequestDto, BindingResult bindingResult, RedirectAttributes redirectAttributes);

    List<Product> getTheProductsToShow(String category);

    String saveEditedProduct(Product product, Model model, String quantityChange, RedirectAttributes redirectAttributes);

    String showSingleId(Model model, Long id);

    String addToBasket(Long productId, int quantity, RedirectAttributes redirectAttributes, boolean isFromAllProducts);

    Order getOrCreateBasketOrder(User user);

    Order getBasketOrder(User user);

    String showSearchByQuantityResults(int minQuantity, int maxQuantity, Model model);

    String showSortedProductsBySortType(String sortType, boolean ascending, Model model, List<Product> products);

    String searchProductByName(String searchString, Model model);

    String showProductsForChosenPrice(String minPrice, String maxPrice, boolean minPriceChanged, boolean maxPriceChanged, RedirectAttributes redirectAttributes, Model model);

    String searchProductsByQuantity(Model model);

    String editProductForm(Long id, Model model);

    String deleteProduct(Long id);

    String undeleteProduct(Long id);

    String showAllDeletedProducts(Model model);

    String showSingleIdIncludingDeleted(Model model, Long id);

    String showAllProducts(String sortType, String category, boolean ascending, String minPrice, String maxPrice, Model model);
}
