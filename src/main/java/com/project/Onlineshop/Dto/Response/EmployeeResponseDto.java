package com.project.Onlineshop.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.Onlineshop.Entity.Role;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private int age;
    private BigDecimal salary;
    private String phoneNumber;
    private Role role;
    private LocalDateTime createdAt;
    private boolean isEnabled;
}
