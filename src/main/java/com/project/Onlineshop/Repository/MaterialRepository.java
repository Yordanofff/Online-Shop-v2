package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.ProductHelpers.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByName(String name);
}
