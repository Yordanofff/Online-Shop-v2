package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_IdAndStatus_Id(Long userId, Long statusId);
}
