package com.project.Onlineshop.Dto.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {
    //TODO - add @ annotations to the USER + DTO
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String repeatedPassword;
}
