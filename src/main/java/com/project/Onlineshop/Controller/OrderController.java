package com.project.Onlineshop.Controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("orderProducts", orderProductRepository.findAll());
        model.addAttribute("products", productRepository.findByIsDeletedFalse());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses",orderStatusRepository.findAll());
        return "orders_all";
    }

    @PostMapping("/changeStatus")
    public String changeOrderStatus(@RequestParam Long orderId, @RequestParam Long statusId, RedirectAttributes redirectAttributes, Model model){
        if(orderRepository.findById(orderId).isPresent()){
            Order order = orderRepository.findById(orderId).get();
            if(orderStatusRepository.findById(statusId).isPresent()){
                OrderStatus status = orderStatusRepository.findById(statusId).get();
                order.setStatus(status);
                orderRepository.save(order);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Order status changed successfully for order ID "+orderId+"!");
        return "redirect:/orders/show";
    }
}
