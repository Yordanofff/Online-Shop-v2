package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.ProductInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductInformation,Long> {
}
