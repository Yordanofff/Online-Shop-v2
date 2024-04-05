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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
        return productRepository.findByNameContainingIgnoreCase(keyword);
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
//            redirectAttributes.addAttribute("productType", productType);
            return "redirect:/products/add?productType=" + productType ;
        }

        if (productType.equalsIgnoreCase("food")) {
            product = productMapper.toFood(productRequestDto);
        } else if (productType.equalsIgnoreCase("drink")) {
            product = productMapper.toDrink(productRequestDto);
        } else if (productType.equalsIgnoreCase("accessory")) {
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

    public String showSingleId(Model model, Long id) {
        String productAddedMessage = (String) model.getAttribute("product_added");
        String stockNotEnoughError = (String) model.getAttribute("no_stock");
        String usersOnlyError = (String) model.getAttribute("users_only_error");

        Product product = productRepository.findById(id).orElse(null);

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
        Product product = productRepository.findById(productId).orElseThrow();

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

        saveNewItemInOrderProduct(order, product, quantity);

        redirectAttributes.addFlashAttribute("product_added", "Added " + quantity + " items to your basket.");

        //  TODO: add button - viewBasket ?
        //  System.out.println("Adding " + quantity + " items from " + productRepository.findById(productId).get());
        // Adding 5 items from Drink(bestBefore=2024-04-01)
        return "redirect:/products/show/" + productId;
    }

    private void addModelAttributesDependingOnProductType(String productType, Model model) {
        //       TODO - the ifs below need to be better looking + validations should be added. Better?
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
    }

    private void saveNewItemInOrderProduct(Order order, Product product, int quantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setQuantity(quantity);
        orderProductRepository.save(orderProduct);
    }

    private Order getOrCreateBasketOrder(User user) {
        OrderStatus basketOrderStatus = OrderStatus.builder()
                .id(OrderStatusType.BASKET.getId())
                .name(OrderStatusType.BASKET.name())
                .build();

        return getOrCreateOrder(user, basketOrderStatus);
    }

    private Order getOrCreateOrder(User user, OrderStatus orderStatus) {
        List<Order> orderListStatusBasket = orderRepository.findAllByUser_IdAndStatus_Id(user.getId(), orderStatus.getId());

        if (orderListStatusBasket.size() > 1) {
            throw new ServerErrorException("Critical server error.More than one basket for user with userID: " + user.getId());
        }

        if (orderListStatusBasket.size() == 1) {
            return orderListStatusBasket.getFirst();
        }

        return createNewOrder(user, orderStatus);
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

}
