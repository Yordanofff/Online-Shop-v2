package com.project.Onlineshop.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    //TODO - add @ annotations to the USER + DTO

    @NotNull
    @Size(min = 3, max = 40)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 40, message = "Last name must be between 3 and 40 characters!")
    private String lastName;

    @NotNull
    @Size(min = 3, max = 30, message = "Username must be between 3 and 40 characters!")
    private String username;

    @NotNull
    @Size(min = 10, max = 30, message = "E-mail must be between 10 and 30 characters!")
    private String email;

    @NotBlank(message = "You must enter a password!")
    private String password;

    @NotBlank(message = "You must repeat the password!")
    private String repeatedPassword;
}
