package com.project.Onlineshop;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Repository.EmployeeRepository;
import com.project.Onlineshop.Repository.RoleRepository;
import com.project.Onlineshop.Repository.UserRepository;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class DataInit implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                Role role = new Role();
                role.setName(roleType.name());
                roleRepository.save(role);
            }
        }

        if (userRepository.count() == 0) { // TODO - use just the service (get all)
            userService.addUser(UserRequestDto.builder()
                    .firstName("Default")
                    .lastName("User")
                    .username("user")
                    .email("user@abv.bg")
                    .password("123")
                    .repeatedPassword("123")
                    .build());
        }

        // Create ADMIN ROLE - custom version of Employee
        if (employeeRepository.count() == 0) {

            Role role_admin = roleRepository.findByName(RoleType.ROLE_ADMIN.name()).orElseThrow();
            Role role_employee = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name()).orElseThrow();

            employeeRepository.save(Employee.builder()
                    .firstName("Default")
                    .lastName("Admin")
                    .email("admin@abv.bg")
                    .password(encoder.encode("123"))
                    .role(role_admin)
                    .createdAt(now())
                    .username("admin")
                    .isEnabled(true)
                    .build());

            employeeRepository.save(Employee.builder()
                    .firstName("Default")
                    .lastName("Employee")
                    .email("employee@abv.bg")
                    .password(encoder.encode("123"))
                    .role(role_employee)
                    .createdAt(now())
                    .username("employee")
                    .isEnabled(true)
                    .build());
        }

    }
}