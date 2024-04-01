package com.project.Onlineshop.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class Role {
    @Id
    private Long id;

    @Column(unique = true)
    private String name;  // Will come from Enum

//    private String permissions; // TODO - not sure if needed
}
