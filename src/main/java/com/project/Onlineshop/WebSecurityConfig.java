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
                        .requestMatchers("/", "/home", "/user/login",
                                "/user/register", "/employee/login", "/employee/register", "/products/show", "/login", "/register", "/photo1.jpg", "/photo2.jpg", "/photo3.jpg").permitAll()
                        .requestMatchers("/show/{id}", "/password/**", "/products/show", "/products/search").hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/employee/profile", "/orders/changeStatus", "/products/add", "/products/save").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/orders/show").hasAnyAuthority("ROLE_EMPLOYEE","ROLE_ADMIN")
                        .requestMatchers("/products/add_to_basket/**", "/user/profile", "/user/basket/**", "/user/updateQuantity", "/user/orders", "/user/cancelOrder/{id}").hasAnyAuthority("ROLE_USER")
                        .requestMatchers("/products/edit/**", "/products/delete/", "/products/undelete", "/product/show/deleted/**", "/products/upload", "/products/searchByPrice", "/products/searchByQuantity").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().permitAll()
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
