package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DefaultController {
    @GetMapping({"/about", "/"})
    String index() {
        return "index";
    }

    @GetMapping("/login")
    String loginChoice() {
        return "login";
    }
    @GetMapping("/register")
    String registerChoice() {
        return "register";
    }
}
