package com.project.Onlineshop;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.ProductHelpers.Brand;
import com.project.Onlineshop.Entity.ProductHelpers.Color;
import com.project.Onlineshop.Entity.ProductHelpers.Material;
import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.UserService;
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
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final BrandRepository brandRepository;

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

        List<Food> foodList = productRepository.getAllFood();
        if (foodList.isEmpty()) {
            productRepository.save(new Food("Баничка", BigDecimal.valueOf(2.10), 10, LocalDate.of(2022, 4, 1)));
        }

        List<Accessories> accessoriesList = productRepository.getAllAccessories();
        if (accessoriesList.isEmpty()) {
            Optional<Color> optionalColor = colorRepository.findById(1L);
            productRepository.save(new Accessories("Cable", BigDecimal.valueOf(2.20), 15, optionalColor.get()));
        }

        List<Drink> drinkList = productRepository.getAllByEntityType(Drink.class);
        if (drinkList.isEmpty()) {
            productRepository.save(new Drink("Ayran", BigDecimal.valueOf(0.80), 30, LocalDate.of(2024, 4, 10)));
            productRepository.save(new Drink("Soda", BigDecimal.valueOf(1), 100, LocalDate.of(2025, 5, 1)));
            productRepository.save(new Drink("Fanta", BigDecimal.valueOf(1.40), 85, LocalDate.of(2024, 4, 1)));
        }

        if (materialRepository.count() == 0) {
            List<String> materials = List.of("Metal", "Wood", "Plastic", "Bamboo", "Cotton", "Foam", "Microfiber", "Silk");
            materials.forEach(material -> materialRepository.save(new Material(material)));
        }

        if (brandRepository.count() == 0) {
            List<String> brands = List.of("Nike", "The CocaCola Company", "Zavet OOD", "Mlin Rz", "Elektroresurs", "RailingSystems LTD", "TheDecorationBrand");
            brands.forEach((brand) -> brandRepository.save(new Brand(brand)));
        }

        List<Railing> railingList = productRepository.getAllByEntityType(Railing.class);
        if (railingList.isEmpty()) {
            Material metal = materialRepository.findByName("Metal").orElseThrow();
            Color red = colorRepository.findByName("Red").orElseThrow();
            Brand brand = brandRepository.findByName("RailingSystems LTD").orElseThrow();
            productRepository.save(Railing.builder()
                    .name("Best in brand railing system")
                    .price(BigDecimal.valueOf(19.99))
                    .quantity(50)
                    .material(metal)
                    .isOutdoor(true)
                    .isNonSlip(true)
                    .color(red)
                    .brand(brand)
                    .build());
        }

        List<Sanitary> sanitaryList = productRepository.getAllByEntityType(Sanitary.class);
        if (sanitaryList.isEmpty()) {
            Material cotton = materialRepository.findByName("Cotton").orElseThrow();
            productRepository.save(new Sanitary("Bodyform Ultra Goodnight Sanitary Towels", BigDecimal.valueOf(11.50), 1000, true, false, cotton));
        }

        List<Decoration> decorations = productRepository.getAllByEntityType(Decoration.class);
        if (decorations.isEmpty()) {
            Material wood = materialRepository.findByName("wood").orElseThrow();
            Brand decoration = brandRepository.findByName("TheDecorationBrand").orElseThrow();
            productRepository.save(new Decoration("Cinvo 30 Pieces Flowers Wood Cutouts Floral Wooden Slices 7cm Unfinished",
                    BigDecimal.valueOf(7.99), 60, wood, decoration));
        }

        List<Product> productList = productRepository.getAllProductsWithQuantityGreaterThan(10);
        for (Product p : productList) {
            System.out.println(p.getName());
        }
    }
}
