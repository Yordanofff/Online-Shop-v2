package com.project.Onlineshop.Dto.Response;

import com.project.Onlineshop.Entity.Address;
import com.project.Onlineshop.Entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private String phoneNumber;
    private Address address;
}
