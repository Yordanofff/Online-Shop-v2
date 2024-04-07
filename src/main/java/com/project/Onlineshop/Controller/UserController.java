package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Service.Implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/user/login")
    String userLogin() {
        return "login_user";
    }

    @GetMapping("/user/register")
    String register(Model model) {
        model.addAttribute("userRequestDto", new UserRequestDto());
        return "register_user";
    }

    @PostMapping("/register")
    String registerNewUser(@ModelAttribute @Valid UserRequestDto userRequestDto, BindingResult bindingResult, Model model) {
        return userService.registerNewUser(userRequestDto, bindingResult, model);
    }

    @GetMapping("/user/profile")
    String showProfile(Model model, Authentication authentication) {
        return userService.showProfile(model, authentication);
    }

    @GetMapping("/user/basket/show")
    public String showBasket(Model model, Authentication authentication) {
        return userService.showBasket(model, authentication);
    }

    @PostMapping("/user/basket/buy")
    public String buyNow(Model model, Authentication authentication, RedirectAttributes redirectAttributes){
        return userService.buyNow(model, authentication, redirectAttributes);
    }

    @PostMapping("/user/updateQuantity")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("orderId") Long orderId,
                                 @RequestParam("quantity") int quantity,
                                 Model model) {
        return userService.updateQuantity(productId, orderId, quantity, model);
    }

}
