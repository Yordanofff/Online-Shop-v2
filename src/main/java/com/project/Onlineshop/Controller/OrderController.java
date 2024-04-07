package com.project.Onlineshop.Controller;

import ch.qos.logback.core.status.Status;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderStatus;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.Implementation.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final OrderStatusRepository orderStatusRepository;

    public OrderController(OrderRepository orderRepository,
                           OrderProductRepository orderProductRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository, UserServiceImpl userService,
                           OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.orderStatusRepository = orderStatusRepository;
    }

    @GetMapping("/show")
    public String showOrders(Model model){
        List<Order> orders = orderRepository.findAll();
        //TODO - when 'Buy now' is pressed in basket page, the price will be calculated
        //the method below won't be needed
        OrderStatus pendingStatus = orderStatusRepository.findByName("PENDING").get();
        for (Order order : orders) {
            if (order.getStatus().equals(pendingStatus)) {
                BigDecimal totalPrice = userService.calculateOrderPrice(order.getId());
                order.setPrice(totalPrice);
            }
        }
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("orderProducts", orderProductRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses",orderStatusRepository.findAll());
        return "orders_all";
    }

    @PostMapping("/changeStatus")
    public String changeOrderStatus(@RequestParam Long orderId, @RequestParam Long statusId){
        if(orderRepository.findById(orderId).isPresent()){
            Order order = orderRepository.findById(orderId).get();
            if(orderStatusRepository.findById(statusId).isPresent()){
                OrderStatus status = orderStatusRepository.findById(statusId).get();
                order.setStatus(status);
                orderRepository.save(order);
            }
        }
        return "redirect:/orders/show";
    }
}
