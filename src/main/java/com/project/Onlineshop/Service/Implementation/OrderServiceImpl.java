package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.OrderRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderProduct;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Exceptions.NotEnoughStockException;
import com.project.Onlineshop.Exceptions.ProductNotFoundException;
import com.project.Onlineshop.Mapper.OrderMapper;
import com.project.Onlineshop.Repository.OrderProductRepository;
import com.project.Onlineshop.Repository.OrderRepository;
import com.project.Onlineshop.Repository.ProductRepository;
import com.project.Onlineshop.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    @Override
    public void addOrder(OrderRequestDto orderRequestDto) {
        // Can also return String to a page - your order has been saved or something like that.
        List<OrderProduct> orderProducts = orderRequestDto.getOrderProducts();
        validateIfAllProductsAreInStock(orderProducts);

        Order order = new Order();
        order = orderMapper.toEntity(orderRequestDto);
        orderRepository.save(order);

        orderProductRepository.saveAll(orderProducts);
    }

    private void validateIfAllProductsAreInStock(List<OrderProduct> orderProducts) {
        for (OrderProduct orderedProduct : orderProducts) {

            Product product = orderedProduct.getProduct();
            int quantity = orderedProduct.getQuantity();

            if (!isProductQuantityInStockEnough(product, quantity)) {
                throw new NotEnoughStockException(product, getStockAmountOfProduct(product));
            }
            // TODO - check all products and return all errors at once - better user experience
            //  will be List<String> erorrs .... .add(this error) then handle the message with model.AddAttribute...
        }
    }

    private boolean isProductQuantityInStockEnough(Product product, int quantityRequired) {
        int quantityInStock = getStockAmountOfProduct(product);
        return quantityInStock > quantityRequired;
    }

    private int getStockAmountOfProduct(Product product) {
        validateProductIsInDB(product.getId());

        // Will exist at this point.
        return productRepository.findById(product.getId()).get().getQuantity();
    }


    private void validateProductIsInDB(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with ID: " + id + " not found in the DB.");
        }
    }
}