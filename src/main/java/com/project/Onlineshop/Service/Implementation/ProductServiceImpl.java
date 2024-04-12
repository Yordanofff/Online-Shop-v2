package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.ProductRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderProduct;
import com.project.Onlineshop.Entity.OrderStatus;
import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Exceptions.ProductStockNotEnoughException;
import com.project.Onlineshop.Exceptions.ServerErrorException;
import com.project.Onlineshop.Mapper.ProductMapper;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Static.OrderStatusType;
import com.project.Onlineshop.Utility.PriceRange;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.project.Onlineshop.Utility.SearchUtility.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

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

    public List<Product> searchProducts(String keyword) {
        String latinKeyword;
        String cyrillicKeyword;
        if (containsCyrillic(keyword)) {
            latinKeyword = convertToLatin(keyword.toLowerCase());
            cyrillicKeyword = keyword.toLowerCase();
        } else {
            latinKeyword = keyword.toLowerCase();
            cyrillicKeyword = convertToCyrillic(latinKeyword);
        }
        return productRepository.findByNameContainingIgnoreCase(latinKeyword, cyrillicKeyword);
    }

    public String addNewProduct(String productType, Model model) {

        //Return 404 if productType is not a class.
        Class<? extends Product> productClass = getProductClass(productType.toUpperCase());
        if (productClass == null) {
            return "404_page_not_found";
        }

        if (!model.containsAttribute("productRequestDto")) {
            model.addAttribute("productRequestDto", new ProductRequestDto());
        }
        model.addAttribute("productType", productType);
        addModelAttributesDependingOnProductType(productType, model);
        return "product_add";
    }

    public String saveProduct(String productType, ProductRequestDto productRequestDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        Product product = new Product();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productRequestDto", bindingResult);
            redirectAttributes.addFlashAttribute("productRequestDto", productRequestDto);
            redirectAttributes.addFlashAttribute("productType", productType);
            redirectAttributes.addAttribute("productType", productType); // this row adds -> ?productType=" + productType
            return "redirect:/products/add";
        }

        if (productType.equalsIgnoreCase("food")) {
            product = productMapper.toFood(productRequestDto);
        } else if (productType.equalsIgnoreCase("drink")) {
            product = productMapper.toDrink(productRequestDto);
        } else if (productType.equalsIgnoreCase("accessories")) {
            product = productMapper.toAccessories(productRequestDto);
        } else if (productType.equalsIgnoreCase("sanitary")) {
            product = productMapper.toSanitary(productRequestDto);
        } else if (productType.equalsIgnoreCase("railing")) {
            product = productMapper.toRailing(productRequestDto);
        } else if (productType.equalsIgnoreCase("decoration")) {
            product = productMapper.toDecoration(productRequestDto);
        } else if (productType.equalsIgnoreCase("others")) {
            product = productMapper.toOthers(productRequestDto);
        }
        productRepository.save(product);
        return "redirect:/products/show";
    }

    public List<Product> getTheProductsToShow(String category) {
        if (Objects.equals(category, "PRODUCT")) {
            return productRepository.findByIsDeletedFalse();
        } else {
            Class<? extends Product> productClass = getProductClass(category);
            if (productClass != null) {
                return (List<Product>) productRepository.getAllByEntityType(productClass);
            } else {
                throw new NullPointerException("The product category " + category + " was not found!");
            }
        }
    }

    private boolean validateEditedProduct(Product product, Model model, int quantityChange, RedirectAttributes redirectAttributes) {
        if (product.getName().length() < 3) {
            redirectAttributes.addFlashAttribute("name_too_short", "You must enter a longer name!");
            return true;
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("price_too_low", "You must enter a positive price!");
            return true;
        }
        if (product.getQuantity() + quantityChange < 0) {
            redirectAttributes.addFlashAttribute("quantity_too_low", "You must enter a quantity so that the stock is at least 0!");
            return true;
        }
        return false;
    }

    public String saveEditedProduct(Product product, Model model, int quantityChange, RedirectAttributes redirectAttributes) {
        if (validateEditedProduct(product, model, quantityChange, redirectAttributes)) {
            return "redirect:/products/edit/" + product.getId();
        }
        Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(product.getId());
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(existingProduct.getQuantity() + quantityChange);  // positive or negative
            productRepository.save(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Stock updated successfully!");
            return "redirect:/products/edit/" + product.getId();
        } else {
            throw new IllegalArgumentException("Product with this ID was not found");
        }
    }

    public String showSingleId(Model model, Long id) {
        String productAddedMessage = (String) model.getAttribute("product_added");
        String stockNotEnoughError = (String) model.getAttribute("no_stock");
        String usersOnlyError = (String) model.getAttribute("users_only_error");

        Product product = productRepository.findByIdNotDeleted(id).orElse(null);

        if (product == null) {
            return "404_page_not_found";
        }

        model.addAttribute("stockNotEnoughError", stockNotEnoughError);
        model.addAttribute("productAddedMessage", productAddedMessage);
        model.addAttribute("usersOnlyError", usersOnlyError);
        model.addAttribute("product", product);
        return "product_view";
    }

    public String addToBasket(Long productId, int quantity, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findByIdNotDeleted(productId).orElseThrow();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // anonymousUser, user, admin etc.
        User user;
        try {
            user = userRepository.findByUsername(username).orElseThrow(); // if authenticated - user should exist.
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("users_only_error", "This operation is only available for Users. Please log in.");
            return "redirect:/products/show/" + productId;
        }

        try {
            validateProductHasEnoughStock(product, quantity);
        } catch (ProductStockNotEnoughException e) {
            redirectAttributes.addFlashAttribute("no_stock", e.getMessage());
            return "redirect:/products/show/" + productId;
        }

        // Find orders for that user that have status Basket. If exist - use it. else - create new order basket
        Order order = getOrCreateBasketOrder(user);

        addNewItemInOrderProductOrAppendToAnExisting(order, product, quantity);

        redirectAttributes.addFlashAttribute("product_added", "Added " + quantity + " items to your basket.");

        //  TODO: add button - viewBasket ?
        return "redirect:/products/show/" + productId;
    }

    private void addModelAttributesDependingOnProductType(String productType, Model model) {
        //       TODO - the ifs below need to be better looking + validations should be added. Better? Yep!
        if (productType.equalsIgnoreCase("Sanitary") || productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Decoration") || productType.equalsIgnoreCase("Others")) {
            model.addAttribute("materials", materialRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Railing") || productType.equalsIgnoreCase("Accessories")) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Decoration")) {
            model.addAttribute("brands", brandRepository.findAll());
        }
        if (productType.equalsIgnoreCase("Others")) {
            model.addAttribute("colors", colorRepository.findAll());
        }
    }

    private List<Product> findAllProductsBetweenTwoPrices(BigDecimal p1, BigDecimal p2) {
        List<Product> products = (List<Product>) productRepository.findByIsDeletedFalse();
        List<Product> resultList = new ArrayList<>();

        PriceRange priceRange = PriceRange.getMinMaxNumber(p1, p2);
        BigDecimal minPrice = priceRange.getMin();
        BigDecimal maxPrice = priceRange.getMax();

        for (Product p : products) {
            BigDecimal price = p.getPrice();
            if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0)
                resultList.add(p);
        }
        return resultList;
    }

    private void addNewItemInOrderProductOrAppendToAnExisting(Order order, Product product, int quantity) {
        // Check if product is already in the basket
        OrderProduct currentOrderProduct = orderProductRepository.findByOrderIdAndProductId(order.getId(), product.getId());
        if (currentOrderProduct == null) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(quantity);
            orderProductRepository.save(orderProduct);
        } else {
            int numberOfThatProductAlreadyInBasket = currentOrderProduct.getQuantity();
            currentOrderProduct.setQuantity(quantity + numberOfThatProductAlreadyInBasket);
            orderProductRepository.save(currentOrderProduct);
        }
    }

    public Order getOrCreateBasketOrder(User user) {
        Order order = getBasketOrder(user);

        if (order != null) {
            return order;
        }

        OrderStatus basketOrderStatus = OrderStatus.builder()
                .id(OrderStatusType.BASKET.getId())
                .name(OrderStatusType.BASKET.name())
                .build();
        return createNewOrder(user, basketOrderStatus);
    }

    public Order getBasketOrder(User user) {
        OrderStatus basketOrderStatus = OrderStatus.builder()
                .id(OrderStatusType.BASKET.getId())
                .name(OrderStatusType.BASKET.name())
                .build();

        List<Order> basket_orders = getUserOrdersByOrderStatus(user, basketOrderStatus);

        if (basket_orders.size() > 1) {
            throw new ServerErrorException("Critical server error.More than one basket for user with userID: " + user.getId());
        }

        if (basket_orders.size() == 1) {
            return basket_orders.getFirst();
        }

        return null;
    }


    private List<Order> getUserOrdersByOrderStatus(User user, OrderStatus orderStatus) {
        return orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), orderStatus.getId());
    }

    private Order createNewOrder(User user, OrderStatus orderStatus) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(orderStatus);
        orderRepository.save(order);
        return order;
    }

    private void validateProductHasEnoughStock(Product product, int quantityRequired) {
        if (product.getQuantity() < quantityRequired) {
            throw new ProductStockNotEnoughException("Not enough stock. Required: " + quantityRequired + " Available: " + product.getQuantity());
        }
    }

    public String showSearchByPriceResults(Model model) {
        BigDecimal minPrice = productRepository.findProductWithLowestPrice().get().getPrice();
        BigDecimal maxPrice = productRepository.findProductWithHighestPrice().get().getPrice();

        model.addAttribute("minPrice", minPrice.toString());
        model.addAttribute("maxPrice", maxPrice.toString());
        return "search_by_price";
    }

    public String showSearchByQuantityResults(int minQuantity, int maxQuantity, Model model) {
        if (minQuantity < 0 || maxQuantity < 0) {
            model.addAttribute("incorrect_quantity", "You must enter positive quantity values!");
            return "search_by_quantity";
        }
        if (maxQuantity < minQuantity) {
            model.addAttribute("min_bigger_than_max", "You must enter a larger number for the minimum quantity than for the maximum quantity!");
            return "search_by_quantity";
        }
        List<Product> result = productRepository.findProductsByQuantityBetween(minQuantity, maxQuantity);
        if (result.isEmpty()) {
            model.addAttribute("list_empty", "No match was found for your search!");
        } else {
            model.addAttribute("search_by_quantity_results", "Showing products having quantity between " + minQuantity + "-" + maxQuantity);
        }
        model.addAttribute("products", result);
        return "products_all";
    }

    public String showSortedProductsBySortType(String sortType, boolean ascending, Model model, List<Product> products) {
        model.addAttribute("sortType", sortType);

        if (sortType.equalsIgnoreCase("byName")) {
            if (ascending) {
                products.sort(Comparator.comparing(Product::getName));
            } else {
                products.sort(Comparator.comparing(Product::getName).reversed());
            }
            model.addAttribute("products", products);
        }

        if (sortType.equalsIgnoreCase("byPrice")) {
            if (ascending) {
                products.sort(Comparator.comparing(Product::getPrice));
            } else {
                products.sort(Comparator.comparing(Product::getPrice).reversed());
            }
            model.addAttribute("products", products);
        }

        if (sortType.equalsIgnoreCase("byExpiryDate")) {
            List<Food> foods = productRepository.getAllByEntityType(Food.class);
            if (ascending) {
                foods.sort(Comparator.comparing(Food::getExpiryDate));
            } else {
                foods.sort(Comparator.comparing(Food::getExpiryDate).reversed());
            }
            model.addAttribute("products", foods);
            model.addAttribute("category", "FOOD"); // set the category too.
        }

        return "products_all";
    }


    public String searchProductByName(String searchString, Model model) {
        if (!searchString.isEmpty()) {
            model.addAttribute("products", searchProducts(searchString));
        }
        model.addAttribute("searchString", searchString);
        return "products_found";
    }

    public String showProductsForChosenPrice(String minPrice, String maxPrice, boolean minPriceChanged, boolean maxPriceChanged, RedirectAttributes redirectAttributes, Model model) {
        if (!minPriceChanged || !maxPriceChanged) {
            redirectAttributes.addFlashAttribute("error", "You must choose both min and max values!");
            model.addAttribute("minPrice", minPrice.toString());
            model.addAttribute("maxPrice", maxPrice.toString());
            return "redirect:/products/searchByPrice";
        }
        BigDecimal minPriceDecimal = new BigDecimal(minPrice);
        BigDecimal maxPriceDecimal = new BigDecimal(maxPrice);

        PriceRange priceRange = PriceRange.getMinMaxNumber(minPriceDecimal, maxPriceDecimal);
        BigDecimal actualMinPrice = priceRange.getMin();
        BigDecimal actualMaxPrice = priceRange.getMax();

        model.addAttribute("products", findAllProductsBetweenTwoPrices(minPriceDecimal, maxPriceDecimal));
        model.addAttribute("search_by_price_results", "(Showing products having a price between " + actualMinPrice + " лв. and " + actualMaxPrice + " лв.)");
        return "products_all";
    }

    public String searchProductsByQuantity(Model model) {
        int minQuantity = productRepository.findProductsByLowestQuantity().getFirst().getQuantity();
        int maxQuantity = productRepository.findProductsWithHighestQuantity().getFirst().getQuantity();
        model.addAttribute("minQuantity", minQuantity);
        model.addAttribute("maxQuantity", maxQuantity);
        return "search_by_quantity";
    }

    public String editProductForm(Long id, Model model) {
        Optional<Product> product = productRepository.findByIdNotDeleted(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product_edit";
        }
        return "404_page_not_found";
    }

    public String deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(id);
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

    public String undeleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);  // DELETED product
        if (optionalProduct.isEmpty()) {
            return "404_page_not_found";
        }
        Product product = optionalProduct.get();
        if (product.isDeleted()) {
            product.setDeleted(false);
            productRepository.save(product);
        }
        return "redirect:/products/show/deleted";
    }

    public String showAllDeletedProducts(Model model) {
        List<Product> deletedProducts = productRepository.findByIsDeletedTrue();
        model.addAttribute("products", deletedProducts);
        return "products_enable_deleted";
    }

    public String showSingleIdIncludingDeleted(Model model, Long id) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return "404_page_not_found";
        }

        model.addAttribute("product", product);
        return "product_view";
    }

    public String showAllProducts(String sortType, String category, boolean ascending,
                                  String minPrice, String maxPrice, Model model) {
        List<Product> products = productRepository.findByIsDeletedFalse(); // Default - show all (enabled only)

        // Show the min max price range - on the sliders. Will keep the same range for all items for now. Keep it simple.
        BigDecimal minPriceRange = getMinPriceOfSelectedProducts(products);
        BigDecimal maxPriceRange = getMaxPriceOfSelectedProducts(products);
        model.addAttribute("minPriceR", minPriceRange.toString());
        model.addAttribute("maxPriceR", maxPriceRange.toString());

        if (category != null) {
            model.addAttribute("category", category);
            products = getTheProductsToShow(category);  // if category is selected - update the list
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
            return showSortedProductsBySortType(sortType, ascending, model, products);
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

    private BigDecimal getMaxPriceOfSelectedProducts(List<Product> products) {
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

    //    public String filterProductsByChosenCategory(String category, Model model) {
    //        if (Objects.equals(category, "PRODUCT")) {
    //            model.addAttribute("products", productRepository.findByIsDeletedFalse());
    //            model.addAttribute("category", category);
    //        } else {
    //            Class<? extends Product> productClass = getProductClass(category);
    //            if (productClass != null) {
    //                model.addAttribute("products", productRepository.getAllByEntityType(productClass));
    //                model.addAttribute("category", category);
    //            } else {
    //                throw new NullPointerException("The product category was not found!");
    //            }
    //        }
    //        return "products_all";
    //    }
}
