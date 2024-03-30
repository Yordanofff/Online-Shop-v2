package com.project.Onlineshop.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT) // reduce the size of the Json output for null/0 values.
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Date when employee was created/hired

    private boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

//    private Address address;  // TODO: Address (street name, number etc.) + City classes
}
