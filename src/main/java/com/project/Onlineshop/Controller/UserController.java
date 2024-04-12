package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Service.Implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserServiceImpl userService;


    @GetMapping("/login")
    String userLogin() {
        return "login_user";
    }

    @GetMapping("/register")
    String register(Model model) {
        return userService.register(model);
    }

    @PostMapping("/register")
    String registerNewUser(@ModelAttribute @Valid UserRequestDto userRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        return userService.registerNewUser(userRequestDto, bindingResult, model, redirectAttributes);
    }

    @GetMapping("/profile")
    String showProfile(Model model, Authentication authentication) {
        return userService.showProfile(model, authentication);
    }

    @GetMapping("/basket/show")
    public String showBasket(Model model, Authentication authentication) {
        return userService.showBasket(model, authentication);
    }

    @PostMapping("/basket/buy")
    public String buyNow(Model model, Authentication authentication, RedirectAttributes redirectAttributes){
        return userService.buyNow(model, authentication, redirectAttributes);
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("orderId") Long orderId,
                                 @RequestParam("quantity") int quantity,
                                 Model model) {
        return userService.updateQuantity(productId, orderId, quantity, model);
    }

    @GetMapping("/orders")
    public String showCurrentUserOrders(Authentication authentication, Model model){
        return userService.showUserOrders(authentication, model);
    }

    @GetMapping("/cancelOrder/{id}")
    public String cancelOrder(@PathVariable("id") Long orderId){
        return userService.changeOrderStatusToCancelled(orderId);
    }
}
