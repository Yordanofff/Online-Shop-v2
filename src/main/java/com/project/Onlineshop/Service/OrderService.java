package com.project.Onlineshop.Service;

import com.project.Onlineshop.Dto.Request.OrderRequestDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface OrderService {
    void addOrder(OrderRequestDto orderRequestDto);
    String showOrders(Model model);
    String changeOrderStatus(@RequestParam Long orderId, @RequestParam Long statusId, RedirectAttributes redirectAttributes, Model model);
    String viewSingleOrder(@PathVariable("id") Long id, Model model);
}
