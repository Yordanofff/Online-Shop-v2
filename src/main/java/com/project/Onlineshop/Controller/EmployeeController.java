package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;


    @GetMapping("/get_all")
    public String listAllEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());  // responseDTO
        return "/employees/get_all";
    }

    @GetMapping("/login")
    String employeeLogin() {
        return "login_employee";
    }


    @GetMapping("/register")
    String registerEmployeeForm(Model model) {
        model.addAttribute("employeeRequestDto", new EmployeeRequestDto());
        return "register_employee";
    }

    @PostMapping("/register")
    String registerNewEmployee(@ModelAttribute @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        return employeeService.registerNewEmployee(employeeRequestDto, bindingResult, model, redirectAttributes);
    }

    @GetMapping("/profile")
    String showProfile(Model model, Authentication authentication) {
        return employeeService.showProfile(model, authentication);
    }

}
