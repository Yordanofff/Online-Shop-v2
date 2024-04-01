package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
