package com.project.Onlineshop.Service;

import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final EmployeeRepository employeeRepository;

    public String showAllEmployees(Model model) {
        //find all employees except admins (they are also employees but they have 'ROLE_ADMIN' as a role)
        model.addAttribute("employees", employeeRepository.findByRole_IdNot(1L));
        return "employees_all";
    }

    public String updateEmployeeStatusAndSalary(Long employeeId, boolean employeeStatus, String salary) {
        if (employeeRepository.findById(employeeId).isPresent()) {
            Employee employee = employeeRepository.findById(employeeId).get();
            employee.setEnabled(employeeStatus);
            if (salary != null && !salary.isEmpty()) {
                BigDecimal salaryValue = new BigDecimal(salary);
                employee.setSalary(salaryValue);
                employeeRepository.save(employee);
                return "redirect:/admin/showEmployees";
            } else {

                return "redirect:/admin/showEmployees";
            }
        } else {
            return "redirect:/admin/showEmployees";
        }
    }
}
