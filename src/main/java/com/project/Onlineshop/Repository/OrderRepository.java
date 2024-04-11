package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_IdAndStatus_Id(Long userId, Long statusId);
    List<Order> findByUserIdAndStatusName(Long userId, String statusName);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status.id != 0")
    List<Order> findOrdersByUserIdAndStatusNotBasket(Long userId);
}
