package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Products.Accessories;
import com.project.Onlineshop.Entity.Products.Food;
import com.project.Onlineshop.Entity.Products.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT f FROM Food f")
    List<Food> getAllFood();

    @Query("SELECT a FROM Accessories a")
    List<Accessories> getAllAccessories();

    @Query("SELECT p FROM Product p WHERE p.quantity > 10")
    List<Product> getAllProductsWithQuantityGreaterThan10();

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Food> findAllBy();

    @Query("SELECT p FROM Product p WHERE p.quantity > :minQuantity")
    List<Product> getAllProductsWithQuantityGreaterThan(@Param("minQuantity") int minQuantity);

//    @Query("SELECT p FROM Product p WHERE TYPE(p) = :entityType")
//    List<Product> getAllByEntityType(@Param("entityType") Class entityType);

    @Query("SELECT p FROM Product p WHERE TYPE(p) = :entityType")
    <T extends Product> List<T> getAllByEntityType(@Param("entityType") Class<T> entityType);

}
