package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    // Will be used to calculate total price and display the basket order for the user.
    @Query("SELECT new com.project.Onlineshop.Entity.OrderProduct(op.product.id, op.product.name, op.product.price, op.quantity) FROM OrderProduct op WHERE op.order.id = :orderId")
    List<OrderProduct> findAllByOrderId(Long orderId);
    //[OrderProduct(id=null, order=null, product=Product(id=1, name=name1, price=2.10, quantity=0, imageLocation=null), quantity=3),
    // OrderProduct(id=null, order=null, product=Product(id=2, name=name2, price=2.20, quantity=0, imageLocation=null), quantity=1)]

    OrderProduct findByOrderIdAndProductId(Long orderId, Long productId);

}
