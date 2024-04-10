package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Products.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.quantity > 10 AND p.isDeleted = FALSE")
    List<Product> getAllProductsWithQuantityGreaterThan10();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:keyword% AND p.isDeleted = FALSE")
    List<Product> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.quantity BETWEEN :minQuantity AND :maxQuantity AND p.isDeleted = FALSE")
    List<Product> findProductsByQuantityBetween(@Param("minQuantity") int minQuantity, @Param("maxQuantity") int maxQuantity);

    @Query("SELECT p FROM Product p WHERE p.quantity > :minQuantity AND p.isDeleted = FALSE")
    List<Product> getAllProductsWithQuantityGreaterThan(@Param("minQuantity") int minQuantity);

    @Query("SELECT p FROM Product p WHERE TYPE(p) = :entityType AND p.isDeleted = FALSE")
    <T extends Product> List<T> getAllByEntityType(@Param("entityType") Class<T> entityType);

    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MIN(p2.price) FROM Product p2) AND p.isDeleted = FALSE")
    Optional<Product> findProductWithLowestPrice();

    @Query("SELECT p FROM Product p WHERE p.price = (SELECT MAX(p2.price) FROM Product p2) AND p.isDeleted = FALSE")
    Optional<Product> findProductWithHighestPrice();

    @Query("SELECT p FROM Product p WHERE p.quantity = (SELECT MIN(p2.quantity) FROM Product p2) AND p.isDeleted = FALSE")
    Optional<Product> findProductWithLowestQuantity();

    @Query("SELECT p FROM Product p WHERE p.quantity = (SELECT MAX(p2.quantity) FROM Product p2) AND p.isDeleted = FALSE")
    Optional<Product> findProductWithHighestQuantity();

    List<Product> findByIsDeletedFalse();

    List<Product> findByIsDeletedTrue();  // TODO - admin/employee page to allow putting items back in stock

    //    private static <T extends Product> List<T> getAllProductsWithStatusNotDeleted(List<T> products) {
    //        List<T> result = new ArrayList<>();
    //        if (products.isEmpty()) {
    //            return products;
    //        }
    //        for (T p : products) {
    //            if (!p.isDeleted()) {
    //                result.add(p);
    //            }
    //        }
    //        return result;
    //    }

    //    List<Food> findAllBy();

    //    @Query("SELECT a FROM Accessories a")
    //    List<Accessories> getAllAccessories();

    //    @Query("SELECT f FROM Food f")
    //    List<Food> getAllFood();
}
