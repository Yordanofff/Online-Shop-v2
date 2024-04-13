package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/showEmployees")
    public String showAllEmployees(Model model) {
        return adminService.showAllEmployees(model);
    }

    @PostMapping("/updateEmployeeStatus")
    public String updateEmployeeStatusAndSalary(@RequestParam Long employeeId, @RequestParam String salary,
                                                @RequestParam(required = false) boolean employeeStatus) {
        return adminService.updateEmployeeStatusAndSalary(employeeId, employeeStatus, salary);
    }
}
