package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/show")
    public String showOrders(Model model) {
        return orderService.showOrders(model);
    }

    @PostMapping("/changeStatus")
    public String changeOrderStatus(@RequestParam Long orderId, @RequestParam Long statusId, RedirectAttributes redirectAttributes, Model model) {
        return orderService.changeOrderStatus(orderId, statusId, redirectAttributes, model);
    }

    @GetMapping("/show/{id}")
    public String viewSingleOrder(@PathVariable("id") Long id, Model model) {
        return orderService.viewSingleOrder(id, model);
    }
}
