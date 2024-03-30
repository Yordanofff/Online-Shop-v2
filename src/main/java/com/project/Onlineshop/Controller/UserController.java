package com.project.Onlineshop.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/user/login")
    String userLogin() {
        return "login_user";
    }
}
