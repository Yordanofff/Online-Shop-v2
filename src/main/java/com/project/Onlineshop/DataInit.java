package com.project.Onlineshop;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Entity.*;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.ProductCategory;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class DataInit implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final ColorRepository colorRepository;
//    private final CategoryRepository categoryRepository;
//    private final FoodRepository foodRepository;
//    private final AccessoriesRepository accessoriesRepository;
    private final ProductRepository productRepository;

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

        if (colorRepository.count() == 0) {
            List<String> colors = List.of("Black", "White", "Green", "Red", "Blue", "Other"); // todo ENUMS
            for (String color : colors) {
                colorRepository.save(Color.builder().name(color).build());
            }
        }

//        if (categoryRepository.count() == 0) {
//            for (ProductCategory c : ProductCategory.values()) {
//                categoryRepository.save(new Category(c.name()));
//            }
//        }


//        ==========================
//        if (foodRepository.count() == 0) {
//            foodRepository.save(new Food("Баничка", BigDecimal.valueOf(2.10), 10, LocalDate.of(2022, 4, 1)));
//        }
//
//        if (accessoriesRepository.count() == 0) {
//            Optional<Color> optionalColor = colorRepository.findById(1L);
//            accessoriesRepository.save(new Accessories("Cable", BigDecimal.valueOf(2.20), 15, optionalColor.get()));
//        }
//        ==========================

        List<Food> foodList = productRepository.getAllFood();
        if (foodList.isEmpty()) {
            productRepository.save(new Food("Баничка", BigDecimal.valueOf(2.10), 10, LocalDate.of(2022, 4, 1)));
        }

        List<Accessories> accessoriesList = productRepository.getAllAccessories();
        if (accessoriesList.isEmpty()) {
            Optional<Color> optionalColor = colorRepository.findById(1L);
            productRepository.save(new Accessories("Cable", BigDecimal.valueOf(2.20), 15, optionalColor.get()));
        }

        List<Product> productList = productRepository.getAllProductsWithQuantityGreaterThan10();
        for (Product p: productList) {
            System.out.println(p.getName());
        }
    }
}
