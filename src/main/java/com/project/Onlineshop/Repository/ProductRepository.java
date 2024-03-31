package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Accessories;
import com.project.Onlineshop.Entity.Food;
import com.project.Onlineshop.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT f FROM Food f")
    List<Food> getAllFood();

    @Query("SELECT a FROM Accessories a")
    List<Accessories> getAllAccessories();


}
