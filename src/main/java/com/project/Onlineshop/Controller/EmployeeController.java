package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Service.EmployeeService;
import com.project.Onlineshop.Service.Implementation.EmployeeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    EmployeeServiceImpl employeeServiceImpl;

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
    String registerEmployeeForm(Model model){
        model.addAttribute("employeeRequestDto", new EmployeeRequestDto());
        return "register_employee";
    }

    @PostMapping("/register")
    String registerNewEmployee(@ModelAttribute @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult, Model model){
        return employeeServiceImpl.registerNewEmployee(employeeRequestDto, bindingResult, model);
    }

    @GetMapping("/profile")
    String showProfile(Model model, Authentication authentication){
        return employeeServiceImpl.showProfile(model, authentication);
    }

}
