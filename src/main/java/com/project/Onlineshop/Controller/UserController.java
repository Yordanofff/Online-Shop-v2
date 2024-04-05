package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Service.Implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/user/login")
    String userLogin() {
        return "login_user";
    }

    @GetMapping("/user/register")
    String register(Model model) {
        model.addAttribute("userRequestDto", new UserRequestDto());
        return "register_user";}

    @PostMapping("/register")
    String registerNewUser(@ModelAttribute @Valid UserRequestDto userRequestDto, BindingResult bindingResult, Model model){
        return userService.registerNewUser(userRequestDto, bindingResult, model);
    }

    @GetMapping("/user/profile")
    String showProfile(Model model, Authentication authentication){
        return userService.showProfile(model, authentication);
    }
}
