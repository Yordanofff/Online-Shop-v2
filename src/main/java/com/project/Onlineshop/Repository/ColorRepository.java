package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.ProductHelpers.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByName(String name);
}
