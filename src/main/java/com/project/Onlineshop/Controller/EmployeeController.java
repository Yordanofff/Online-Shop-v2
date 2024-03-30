package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Repository.EmployeeRepository;
import com.project.Onlineshop.Service.EmployeeService;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    @GetMapping("/get_all")
    public String listAllEmployees(Model model){
        model.addAttribute("employees", employeeService.getAllEmployees());  // responseDTO
        return "/employees/get_all";
    }
}
