package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeRepository employeeRepository;

    public AdminController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/showEmployees")
    public String showAllEmployees(Model model){
        //find all employees except admins (they are also employees but they have 'ROLE_ADMIN' as a role)
        model.addAttribute("employees", employeeRepository.findByRole_IdNot(1L));
        return "employees_all";
    }

    @PostMapping("/updateEmployeeStatus")
    public String updateEmployeeStatus(@RequestParam Long employeeId, @RequestParam boolean employeeStatus){
        if(employeeRepository.findById(employeeId).isPresent()){
            Employee employee = employeeRepository.findById(employeeId).get();
            employee.setEnabled(employeeStatus);
            employeeRepository.save(employee);
            System.out.println("employee new status:"+employeeStatus);
            return "redirect:/admin/showEmployees";
        } else {
            return "404_page_not_found";
        }
    }
}
