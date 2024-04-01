package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.ProductHelpers.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    Optional<Brand> findByName(String name);
}
