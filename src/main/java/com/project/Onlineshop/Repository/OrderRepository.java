package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
