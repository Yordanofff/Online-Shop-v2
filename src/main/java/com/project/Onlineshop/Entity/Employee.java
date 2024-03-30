package com.project.Onlineshop.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT) // reduce the size of the Json output for null/0 values.
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @Column(unique = true)
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format.")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    private LocalDate dateOfBirth;
    private BigDecimal salary;

    @Column(unique = true, nullable = true)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;  // Many employees can have one role.

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Date when employee was created/hired

    private boolean isEnabled;
}
