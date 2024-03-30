package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Dto.Response.EmployeeResponseDto;
import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Mapper.EmployeeMapper;
import com.project.Onlineshop.Repository.EmployeeRepository;
import com.project.Onlineshop.Repository.RoleRepository;
import com.project.Onlineshop.Service.EmployeeService;
import com.project.Onlineshop.Static.RoleType;
//import com.project.Onlineshop.Utility.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;


    @Override
    public EmployeeResponseDto getEmployeeByID(Long id) {
        Employee employee = validateEmployeeExistsById(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDto create(EmployeeRequestDto employeeRequestDto) {
        if (isEmailInDB(employeeRequestDto.getEmail())) {
            throw new RuntimeException("Email already in use. Please use a different email");
        }
        if (employeeRequestDto.getPhoneNumber() != null && isPhoneNumberInDB(employeeRequestDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use. Please use a different phone number or leave blank");
        }

        validatePasswordsAreMatching(employeeRequestDto);

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE.name());
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Employee role not found in the DB");
        }

        try {
            Employee employee = employeeMapper.toEntity(employeeRequestDto);
            employee.setRole(optionalRole.get());
            employee.setPassword(encoder.encode(employeeRequestDto.getPassword()));
            employeeRepository.save(employee);
            return employeeMapper.toDto(employee);
        } catch (Exception exception) {
            throw new RuntimeException("An internal error occurred. Please try again. " + exception.getMessage());
        }
    }

    @Override
    public EmployeeResponseDto editEmployeeByID(Long id, EmployeeRequestDto employeeRequestDto) {

        Employee employee = validateEmployeeExistsById(id);
        validateEmailIsNotUsedByAnotherEmployee(id, employeeRequestDto);
        validatePhoneNumberIsNotUsedByAnotherEmployee(id, employeeRequestDto);
        validatePasswordsAreMatching(employeeRequestDto);  // TODO: create another DTO for edit - without password + another one just for password (old + new x 2)

        try {
            Employee newDataForEmployee = employeeMapper.toEntity(employeeRequestDto);
            newDataForEmployee.setId(employee.getId());
            newDataForEmployee.setRole(employee.getRole());  // keep same role  // TODO: new method to update the role for user
            newDataForEmployee.setPassword(encoder.encode(employeeRequestDto.getPassword())); // TODO: delete this once there's another DTO for password

            employeeRepository.save(newDataForEmployee);
            return employeeMapper.toDto(newDataForEmployee);
        } catch (Exception exception) {
            throw new RuntimeException("An internal error occurred. Please try again. " + exception.getCause());
        }
    }

    private void validatePasswordsAreMatching(EmployeeRequestDto employeeRequestDto) {
        if (!employeeRequestDto.getPassword().equals(employeeRequestDto.getRepeatedPassword())) {
            throw new RuntimeException("Passwords don't match");
        }
    }

    private void validatePasswordsAreMatching(String pw1, String pw2) {
        if (!pw1.equals(pw2)) {
            throw new RuntimeException("Passwords don't match");
        }
    }

    private void validatePhoneNumberIsNotUsedByAnotherEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee e = getEmployeeByPhoneNumber(employeeRequestDto.getPhoneNumber());
        if (e != null) {
            // Employee with this email exist
            if (!e.getId().equals(id)) {
                throw new RuntimeException("Another employee is registered with this Phone number: " + employeeRequestDto.getPhoneNumber());
            }
        }

    }

    private void validateEmailIsNotUsedByAnotherEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee e = getEmployeeByEmail(employeeRequestDto.getEmail());
        if (e != null) {
            // Employee with this email exist
            if (!e.getId().equals(id)) {
                throw new RuntimeException("Another employee is registered with this Email: " + employeeRequestDto.getEmail());
            }
        }

    }

    private Employee validateEmployeeExistsById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("Employee with ID: " + id + " not found!");
        }
        return optionalEmployee.get();
    }

    private boolean isEmailInDB(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    private Employee getEmployeeByEmail(String email) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        return optionalEmployee.orElse(null);
    }

    private Employee getEmployeeByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        Optional<Employee> optionalEmployee = employeeRepository.findByPhoneNumber(phoneNumber);
        return optionalEmployee.orElse(null);
    }

    private boolean isPhoneNumberInDB(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
