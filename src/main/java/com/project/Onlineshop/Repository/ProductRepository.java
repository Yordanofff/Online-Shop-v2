package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Accessories;
import com.project.Onlineshop.Entity.Food;
import com.project.Onlineshop.Entity.Product;
import com.project.Onlineshop.Static.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT f FROM Food f")
    List<Food> getAllFood();

    @Query("SELECT a FROM Accessories a")
    List<Accessories> getAllAccessories();

    @Query("SELECT p FROM Product p WHERE p.quantity > 10")
    List<Product> getAllProductsWithQuantityGreaterThan10();

}
