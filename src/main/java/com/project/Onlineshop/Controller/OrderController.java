package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderProduct;
import com.project.Onlineshop.Entity.OrderStatus;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;

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
                if(status.getName().equalsIgnoreCase("DELIVERED")){
                    order.setOrderDeliveryDateTime(LocalDateTime.now());
                }
                orderRepository.save(order);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Order status changed successfully for order ID "+orderId+"!");
        return "redirect:/orders/show";
    }

    @GetMapping("/show/{id}")
    public String viewSingleOrder(@PathVariable("id") Long id, Model model) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()){
            return "404_page_not_found";
        }
        Order order = optionalOrder.get();
        User user = userRepository.findById(order.getUser().getId()).get();
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrderId(order.getId());

        // Created a map of productId and productPurchasePrice as the repository is not working correctly atm.
        List<Object[]> productIdAndProductPrices = orderProductRepository.findProductIdAndProductPricesByOrderId(order.getId());
        Map<Long, BigDecimal> productIdToProductPriceMap = new HashMap<>();
        for (Object[] row : productIdAndProductPrices) {
            Long productId = (Long) row[0];
            BigDecimal productPrice = (BigDecimal) row[1];
            productIdToProductPriceMap.put(productId, productPrice);
        }

        model.addAttribute("order", order);
        model.addAttribute("statuses",orderStatusRepository.findAll());
        model.addAttribute("user", user);
        model.addAttribute("orderProducts", orderProductList);
        model.addAttribute("productIdToProductPriceMap", productIdToProductPriceMap);

        return "orders_single";
    }
}
