package com.project.Onlineshop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/admin/**", "/employee/get_all").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/employee/get_all").denyAll()

                                .requestMatchers("/employee/**").hasAuthority("ROLE_EMPLOYEE")

                                .requestMatchers("/products/edit", "/products/delete", "/products/undelete", "/products/show/deleted", "/products/show/delete/**", "/products/add",
                                        "/products/save", "/products/upload", "/products/searchByPrice", "/products/searchByQuantity", "/orders/changeStatus", "/orders/show" ).hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")

//                                .requestMatchers("/password/**").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_USER")
//
//                                .requestMatchers("/user/**", "/products/add_to_basket", "/products/add_to_basket/**", "/products/").hasAuthority("USER")
                                .requestMatchers("/employee/login").permitAll()
                                .anyRequest().permitAll()

//                                .requestMatchers("/products/edit", "/products/edit/**", "/products/delete", "/products/undelete",
//                                        "/products/show/deleted", "/products/show/deleted/**", "/products/add", "/products/save", "/products/upload",
//                                        "/products/searchByPrice", "/products/searchByQuantity").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // Тези отдолу трябва да са достъпни от всички
                            // /employee /login /register
                            // /about /login /register
                            // /products+ /show /show/{id} /search
                            // /orders /show /show/{id}
                            // /user + /login /register
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("emailOrUsername")  // this is needed in the html form.
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/products/show")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll()
                        .logoutSuccessUrl("/login?loggedOut=true"));

        return http.build();
    }
    // /products/show  + /**

}
