package com.project.Onlineshop.Controller;

import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
