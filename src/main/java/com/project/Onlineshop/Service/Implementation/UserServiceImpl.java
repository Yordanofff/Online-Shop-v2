package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Dto.Response.UserResponseDto;
import com.project.Onlineshop.Entity.*;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Exceptions.EmailInUseException;
import com.project.Onlineshop.Exceptions.PasswordsNotMatchingException;
import com.project.Onlineshop.Exceptions.ServerErrorException;
import com.project.Onlineshop.Exceptions.UsernameInUseException;
import com.project.Onlineshop.Mapper.UserMapper;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.OrderStatusType;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final OrderProductRepository orderProductRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductServiceImpl productService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        if (isEmailInDB(userRequestDto.getEmail())) {
            throw new EmailInUseException("Email already in use. Please use a different email");
        }
        if (isUsernameInDB(userRequestDto.getUsername())) {
            throw new UsernameInUseException("Username already in use. Please use a different username");
        }
        validatePasswordsAreMatching(userRequestDto);

        Role userRole = new Role(RoleType.ROLE_USER.getId(), RoleType.ROLE_USER.name());

        try {
            User user = userMapper.toEntity(userRequestDto);
            user.setRole(userRole);
            user.setPassword(encoder.encode(userRequestDto.getPassword()));
            userRepository.save(user);
            return userMapper.toDto(user);
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again. " + exception.getMessage());
        }
    }

    private boolean isEmailInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameInDB(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private void validatePasswordsAreMatching(UserRequestDto userRequestDto) {
        if (!userRequestDto.getPassword().equals(userRequestDto.getRepeatedPassword())) {
            throw new PasswordsNotMatchingException("Passwords don't match");
        }
    }

    public String registerNewUser(UserRequestDto userRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRequestDto", userRequestDto);
            return "register_user";
        }
        try {
            addUser(userRequestDto);
        } catch (EmailInUseException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("email_error", e.getMessage());
            return "register_user";
        } catch (UsernameInUseException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("user_error", e.getMessage());
            return "register_user";
        } catch (PasswordsNotMatchingException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("password_error", e.getMessage());
            return "register_user";
        }
        redirectAttributes.addFlashAttribute("success", "Account created successfully!");
        return "redirect:/user/register";
    }

    public String showProfile(Model model, Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Order> orderList = orderRepository.findByUserIdAndStatusName(user.getId(), "DELIVERED");
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("orderedProducts", orderProductRepository.findAll());
        model.addAttribute("orderStatuses", orderStatusRepository.findAll());
        model.addAttribute("orders", orderList);
        model.addAttribute("products", productRepository.findByIsDeletedFalse());
        return "profile";
    }



    public String showBasket(Model model, Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Order basketOrder = productService.getBasketOrder(user);
        model.addAttribute("userDetails", userDetails);
        if (basketOrder != null) {
            model.addAttribute("order_id", basketOrder.getId());
            model.addAttribute("orderedProducts", orderProductRepository.findAllByOrderId(basketOrder.getId()));
            model.addAttribute("totalPrice", calculateOrderPrice(basketOrder.getId()));
        }
        model.addAttribute("noProductsInBasket", "");
        return "basket";
    }

    public String updateQuantity(Long productId, Long orderId, int quantity, Model model) {
        updateProductQuantity(orderId, productId, quantity);

        List<OrderProduct> updatedOrderedProducts = orderProductRepository.findAllByOrderId(orderId);

        // Calculate the new total price of the order if needed (assuming calculateOrderPrice is a method that calculates the total price)
        BigDecimal totalPrice = calculateOrderPrice(orderId);

        // Add the updated data to the model
        model.addAttribute("order_id", orderId);
        model.addAttribute("orderedProducts", updatedOrderedProducts);
        model.addAttribute("totalPrice", totalPrice);

        return "redirect:/user/basket/show";
    }

    public String buyNow(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Order basketOrder = productService.getBasketOrder(user);

        Long orderId = basketOrder.getId();
        BigDecimal totalPrice = calculateOrderPrice(orderId);

        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder_Id(basketOrder.getId());

        validateEnoughStockForTheOrder(model, redirectAttributes, orderProducts, orderId, totalPrice);

        // BELOW - IF ALL STOCK AVAILABLE:

        reduceStockQuantity(orderProducts);
        saveThePurchasePriceInOrderProduct(orderProducts);

        // Change the order status to "Pending"
        OrderStatus pending = new OrderStatus(OrderStatusType.PENDING.getId(), OrderStatusType.PENDING.name());
        basketOrder.setStatus(pending);
        basketOrder.setPrice(totalPrice);  // not really needed anymore as it can be calculated based on product price at purchase..
        orderRepository.save(basketOrder);

        redirectAttributes.addFlashAttribute("success", "Order added successfully.");

        return "redirect:/user/basket/show";
    }

    private void saveThePurchasePriceInOrderProduct(List<OrderProduct> orderProducts){
        // Add the price that the item was bought - for every item in the current order.
        for (OrderProduct op : orderProducts) {
            //OrderProduct(id=null, order=null, product=Product(id=1, name=name1, price=2.10, quantity=0, imageLocation=null), quantity=3)
            Product productInStock = productRepository.findByIdNotDeleted(op.getProduct().getId()).get();
            op.setProductPriceWhenPurchased(productInStock.getPrice());
            orderProductRepository.save(op);
        }
    }

    private void reduceStockQuantity(List<OrderProduct> orderProducts){
        // Reduce the quantity in the DB
        for (OrderProduct op : orderProducts) {
            Product productInStock = productRepository.findByIdNotDeleted(op.getProduct().getId()).get();
            productInStock.setQuantity(productInStock.getQuantity() - op.getQuantity());
            productRepository.save(productInStock);
        }
    }

    private String validateEnoughStockForTheOrder(Model model, RedirectAttributes redirectAttributes,
                                                  List<OrderProduct> orderProducts, Long orderId, BigDecimal totalPrice){
        // Looping over each product and checking if there's enough quantity of it for the order.
        List<String> errors = new ArrayList<>();
        for (OrderProduct op : orderProducts) {
            Product productInStock = productRepository.findByIdNotDeleted(op.getProduct().getId()).get();
            if (productInStock.getQuantity() < op.getQuantity()) {
                String errorMessage = op.getProduct().getName() + " (Available: " + productInStock.getQuantity() + ")";  // brackets used in thymeleaf to find the value.
                errors.add(op.getProduct().getId() + "-" + errorMessage);
            }
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("productsNotFound", errors);
            model.addAttribute("order_id", orderId);
            model.addAttribute("orderedProducts", orderProducts);
            model.addAttribute("totalPrice", totalPrice);
            return "redirect:/user/basket/show";
        }
        return null;
    }

    public BigDecimal calculateOrderPrice(Long orderId) {
        List<OrderProduct> allProducts = orderProductRepository.findAllByOrderId(orderId);
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (OrderProduct op : allProducts) {
            BigDecimal productPrice = op.getProduct().getPrice();
            BigDecimal quantity = BigDecimal.valueOf(op.getQuantity());
            totalPrice = totalPrice.add(productPrice.multiply(quantity));
        }
        return totalPrice;
    }

    private void updateProductQuantity(Long orderId, Long productId, int newQuantity) {

        if (newQuantity < 0) {
            newQuantity = 0;
        }
        Order order = orderRepository.findById(orderId).get();
        List<OrderProduct> currentProducts = orderProductRepository.findAllByOrderId(orderId);
        for (OrderProduct op : currentProducts) {
            if (Objects.equals(op.getProduct().getId(), productId)) {
                Long currentOrderProductId = orderProductRepository.findByOrderIdAndProductId(orderId, productId).getId();
                if (newQuantity == 0) {
                    orderProductRepository.deleteById(currentOrderProductId);
                } else {
                    op.setId(currentOrderProductId);
                    op.setQuantity(newQuantity);
                    op.setOrder(order);
                    orderProductRepository.save(op);
                }
                return;
            }
        }

    }
}
