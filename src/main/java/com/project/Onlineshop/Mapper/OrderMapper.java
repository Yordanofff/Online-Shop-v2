package com.project.Onlineshop.Mapper;

import com.project.Onlineshop.Dto.Request.OrderRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderStatus;
import com.project.Onlineshop.Static.OrderStatusType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        OrderStatus pending = new OrderStatus(OrderStatusType.PENDING.getId(), OrderStatusType.PENDING.name());
        order.setStatus(pending);
        order.setUser(orderRequestDto.getUser());
        return order;
    }
}
