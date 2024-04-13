package com.project.Onlineshop.Service;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Dto.Response.UserResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Service
public interface UserService {
    UserResponseDto addUser(UserRequestDto userRequestDto);
    String register(Model model);
    String registerNewUser(UserRequestDto userRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);
    String showProfile(Model model, Authentication authentication);
    String showBasket(Model model, Authentication authentication);
    String updateQuantity(Long productId, Long orderId, int quantity, Model model);
    String buyNow(Model model, Authentication authentication, RedirectAttributes redirectAttributes);
    BigDecimal calculateOrderPrice(Long orderId);
    String showUserOrders(Authentication authentication, Model model);
    String changeOrderStatusToCancelled(Long orderId);
}
