package com.project.Onlineshop;

import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Repository.EmployeeRepository;
import com.project.Onlineshop.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String loginType = request.getParameter("loginType");

        if ("employee".equals(loginType)) {
            return loadEmployeeByEmailOrUsername(usernameOrEmail);
        }

        // Default to user login if loginType is not specified or invalid
        return loadUserByEmailOrUsername(usernameOrEmail);
    }


    private UserDetails loadUserByEmailOrUsername(String emailOrUsername) {
        Optional<User> optionalUser = userRepository.findByEmail(emailOrUsername);
        if (optionalUser.isPresent()) {
            return new MyUserDetails(optionalUser.get());
        }
        optionalUser = userRepository.findByUsername(emailOrUsername);
        if (optionalUser.isPresent()) {
            return new MyUserDetails(optionalUser.get());
        }
        throw new UsernameNotFoundException("Could not find user");
    }

    private UserDetails loadEmployeeByEmailOrUsername(String emailOrUsername) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(emailOrUsername);
        if (optionalEmployee.isPresent()) {
            return new MyUserDetails(optionalEmployee.get());
        }
        optionalEmployee = employeeRepository.findByUsername(emailOrUsername);
        if (optionalEmployee.isPresent()) {
            return new MyUserDetails(optionalEmployee.get());
        }
        throw new UsernameNotFoundException("Could not find employee");
    }
}
