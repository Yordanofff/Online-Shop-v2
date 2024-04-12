package com.project.Onlineshop;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Entity.*;
import com.project.Onlineshop.Entity.ProductHelpers.Brand;
import com.project.Onlineshop.Entity.ProductHelpers.Color;
import com.project.Onlineshop.Entity.ProductHelpers.Material;
import com.project.Onlineshop.Entity.Products.*;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.BulgarianCity;
import com.project.Onlineshop.Static.JobType;
import com.project.Onlineshop.Static.OrderStatusType;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private final OrderStatusRepository orderStatusRepository;
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeOrderStatuses();
        initializeRoles();
        initializeCities();
        initializeProductHelpers();

        initializeDefaultUsers();
        initializeDefaultEmployees();
        initializeProducts();
    }

    private void initializeOrderStatuses() {
        if (orderStatusRepository.count() == 0) {
            for (OrderStatusType orderStatusType : OrderStatusType.values()) {
                orderStatusRepository.save(OrderStatus.builder()
                        .id(orderStatusType.getId())
                        .name(orderStatusType.name())
                        .build());
            }
        }
    }

    public void initializeCities() {
        if (cityRepository.count() == 0) {
            for (BulgarianCity city : BulgarianCity.values()) {
                cityRepository.save(new City(city.getId(), city.getName()));
            }
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                Role role = new Role();
                role.setId(roleType.getId());
                role.setName(roleType.name());
                roleRepository.save(role);
            }
        }
    }

    private void initializeDefaultUsers() {
        if (userRepository.count() == 0) { // TODO - use just the service (get all)
            BulgarianCity razgrad = BulgarianCity.RAZGRAD;

//            City razgradCity = City.builder().id(razgrad.getId()).name(razgrad.getName()).build();
//            Address address1 = Address.builder()
//                    .city(razgradCity)
//                    .streetName("ul. Ivan Asen 8")
//                    .additionalInformation("The green door on the left.")
//                    .build();
//            addressRepository.save(address1);
//
//            Address address2 = Address.builder()
//                    .city(razgradCity)
//                    .streetName("ul. Petar Beron 22")
//                    .additionalInformation("The entrance is behind the bank")
//                    .build();
//            addressRepository.save(address2);

            userService.addUser(UserRequestDto.builder()
                    .firstName("Default")
                    .lastName("User")
                    .username("user")
                    .email("user@abv.bg")
                    .password("123")
                    .repeatedPassword("123")
                    .phoneNumber("0881231234")
//                    .address(address1)
                    .cityId(razgrad.getId())
                    .streetName("ul. Ivan Asen 8")
                    .additionalInformation("The green door on the left.")
                    .build());

            userService.addUser(UserRequestDto.builder()
                    .firstName("Default")
                    .lastName("User2")
                    .username("user2")
                    .email("user2@abv.bg")
                    .password("123")
                    .repeatedPassword("123")
                    .phoneNumber("08899223355")
//                    .address(address2)
                    .cityId(razgrad.getId())
                    .streetName("ul. Petar Beron 22")
                    .additionalInformation("The entrance is behind the bank")
                    .build());
        }
    }

    private void initializeDefaultEmployees() {
        // Create ADMIN ROLE - custom version of Employee
        if (employeeRepository.count() == 0) {

            Role roleAdmin = new Role(RoleType.ROLE_ADMIN.getId(), RoleType.ROLE_ADMIN.name());
            Role roleEmployee = new Role(RoleType.ROLE_EMPLOYEE.getId(), RoleType.ROLE_EMPLOYEE.name());
            employeeRepository.save(Employee.builder()
                    .firstName("Default")
                    .lastName("Admin")
                    .email("admin@abv.bg")
                    .password(encoder.encode("123"))
                    .role(roleAdmin)
                    .createdAt(now())
                    .username("admin")
                    .isEnabled(true)
                    .jobType(JobType.ADMIN)
                    .build());

            employeeRepository.save(Employee.builder()
                    .firstName("Default")
                    .lastName("Employee")
                    .email("employee@abv.bg")
                    .password(encoder.encode("123"))
                    .role(roleEmployee)
                    .createdAt(now())
                    .username("employee")
                    .isEnabled(true)
                    .jobType(JobType.TEST)
                    .build());
        }
    }

    private void initializeProductHelpers() {
        if (colorRepository.count() == 0) {
            List<String> colors = List.of("Black", "White", "Green", "Red", "Blue", "Other"); // todo ENUMS
            for (String color : colors) {
                colorRepository.save(Color.builder().name(color).build());
            }
        }
        if (materialRepository.count() == 0) {
            List<String> materials = List.of("Metal", "Wood", "Plastic", "Bamboo", "Cotton", "Foam", "Microfiber", "Silk", "Paper");
            materials.forEach(material -> materialRepository.save(new Material(material)));
        }
        if (brandRepository.count() == 0) {
            List<String> brands = List.of("Nike", "The CocaCola Company", "Zavet OOD", "Mlin Rz", "Elektroresurs", "RailingSystems LTD", "TheDecorationBrand");
            brands.forEach((brand) -> brandRepository.save(new Brand(brand)));
        }
    }

    private void initializeProducts() {
        Color black = colorRepository.findByName("Black").orElseThrow();
        Color white = colorRepository.findByName("White").orElseThrow();
        Color red = colorRepository.findByName("Red").orElseThrow();
        Material wood = materialRepository.findByName("wood").orElseThrow();
        Brand decoration = brandRepository.findByName("TheDecorationBrand").orElseThrow();
        Material cotton = materialRepository.findByName("Cotton").orElseThrow();
        Material paper = materialRepository.findByName("Paper").orElseThrow();

        List<Food> foodList = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Food.class);
        if (foodList.isEmpty()) {
            productRepository.save(new Food("Баничка", BigDecimal.valueOf(2.10), 10, LocalDate.of(2022, 4, 1), "banica.jpg"));
            productRepository.save(new Food("Козунак", BigDecimal.valueOf(2.00), 10, LocalDate.of(2023, 10, 11), "kozunak.jpg"));
            productRepository.save(new Food("Боб", BigDecimal.valueOf(8.00), 20, LocalDate.of(2023, 10, 14), "bob.jpg"));
            productRepository.save(new Food("Солети", BigDecimal.valueOf(0.75), 200, LocalDate.of(2025, 10, 20), "soleti.png"));

        }

        List<Accessories> accessoriesList = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Accessories.class);
        if (accessoriesList.isEmpty()) {
            Brand brand = brandRepository.findByName("Elektroresurs").orElseThrow();
            productRepository.save(new Accessories("Cable", BigDecimal.valueOf(2.20), 15, black, brand, "cable.jpg"));
            productRepository.save(new Accessories("Network Cable", BigDecimal.valueOf(1.20), 150, white, brand, "cat5.jpg"));
            productRepository.save(new Accessories("Power cord", BigDecimal.valueOf(2.40), 200, black, brand, "power_cord.jpg"));
        }

        List<Drink> drinkList = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Drink.class);
        if (drinkList.isEmpty()) {
            productRepository.save(new Drink("Ayran", BigDecimal.valueOf(0.80), 30, LocalDate.of(2024, 4, 10), "ayran.png"));
            productRepository.save(new Drink("Soda", BigDecimal.valueOf(1), 100, LocalDate.of(2025, 5, 1), "soda.jpg"));
            productRepository.save(new Drink("Fanta", BigDecimal.valueOf(1.40), 85, LocalDate.of(2024, 4, 1), "fanta.jpg"));
            productRepository.save(new Drink("Stela Artois 4x330ml", BigDecimal.valueOf(7.40), 100, LocalDate.of(2028, 10, 6), "stela.png"));
        }

        List<Railing> railingList = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Railing.class);
        if (railingList.isEmpty()) {
            Material metal = materialRepository.findByName("Metal").orElseThrow();
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
                    .imageLocation("railing.jpeg")
                    .build());

            productRepository.save(Railing.builder()
                    .name("Industrial stair hand rail, staircase, cast iron, wrought iron, vintage")
                    .price(BigDecimal.valueOf(20.95))
                    .quantity(100)
                    .material(metal)
                    .isOutdoor(false)
                    .isNonSlip(true)
                    .color(black)
                    .brand(brand)
                    .imageLocation("railing-2.jpeg")
                    .build());
        }

        List<Sanitary> sanitaryList = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Sanitary.class);
        if (sanitaryList.isEmpty()) {
            productRepository.save(new Sanitary("Bodyform Ultra Goodnight Sanitary Towels", BigDecimal.valueOf(11.50), 1000, true, false, cotton, "bodyfoam.jpg"));
            productRepository.save(new Sanitary("Cotton Reusable Menstrual Pads Women Breathable Sanitary Napkin Panty Liners", BigDecimal.valueOf(6.50), 300, true, true, cotton, "menstrual_pads.jpg"));
            productRepository.save(new Sanitary("Paper Napkins Disposable Serviettes Tissue For Birthday ", BigDecimal.valueOf(2.90), 2000, true, false, paper, "napkins.jpg"));
        }

        List<Decoration> decorations = productRepository.getAllByEntityTypeIncludingDeletedForDataInit(Decoration.class);
        if (decorations.isEmpty()) {
            productRepository.save(new Decoration("Cinvo 30 Pieces Flowers Wood Cutouts Floral Wooden Slices 7cm Unfinished",
                    BigDecimal.valueOf(7.99), 60, wood, decoration, "wooden.jpg"));
            productRepository.save(new Decoration("12\" Marble Latex Balloons Birthday, Wedding, Baby Shower Theme Party Decoration",
                    BigDecimal.valueOf(1.99), 60, wood, decoration, "baloons.jpg"));
        }


    }
}
