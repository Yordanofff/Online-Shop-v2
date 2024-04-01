package com.project.Onlineshop.Static;

import lombok.Getter;

@Getter
public enum OrderStatusType {
    PENDING(1),
    PROCESSING(2),
    SHIPPED(3),
    OUT_FOR_DELIVERY(4),
    DELIVERED(5),
    CANCELLED(6),
    ON_HOLD(7),
    RETURNED(8),
    REFUNDED(9);

    private final long id;

    OrderStatusType(long id) {
        this.id = id;
    }
}
