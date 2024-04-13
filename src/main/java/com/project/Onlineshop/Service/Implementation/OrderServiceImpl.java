package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.OrderRequestDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.OrderProduct;
import com.project.Onlineshop.Entity.OrderStatus;
import com.project.Onlineshop.Entity.Products.Product;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Exceptions.NotEnoughStockException;
import com.project.Onlineshop.Exceptions.ProductNotFoundException;
import com.project.Onlineshop.Mapper.OrderMapper;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;

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

    @Override
    public String showOrders(Model model){
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("orderProducts", orderProductRepository.findAll());
        model.addAttribute("products", productRepository.findByIsDeletedFalse());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses",orderStatusRepository.findAll());
        return "orders_all";
    }

    @Override
    public String changeOrderStatus(Long orderId, Long statusId, RedirectAttributes redirectAttributes, Model model) {
        if(orderRepository.findById(orderId).isPresent()){
            Order order = orderRepository.findById(orderId).get();
            if(orderStatusRepository.findById(statusId).isPresent()){
                OrderStatus status = orderStatusRepository.findById(statusId).get();
                order.setStatus(status);
                if(status.getName().equalsIgnoreCase("DELIVERED")){
                    order.setOrderDeliveryDateTime(LocalDateTime.now());
                }
                orderRepository.save(order);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Order status changed successfully for order ID "+orderId+"!");
        return "redirect:/orders/show";
    }

    @Override
    public String viewSingleOrder(Long id, Model model) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()){
            return "404_page_not_found";
        }
        Order order = optionalOrder.get();
        User user = userRepository.findById(order.getUser().getId()).get();
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrderId(order.getId());

        // Created a map of productId and productPurchasePrice as the repository is not working correctly atm.
        List<Object[]> productIdAndProductPrices = orderProductRepository.findProductIdAndProductPricesByOrderId(order.getId());
        Map<Long, BigDecimal> productIdToProductPriceMap = new HashMap<>();
        for (Object[] row : productIdAndProductPrices) {
            Long productId = (Long) row[0];
            BigDecimal productPrice = (BigDecimal) row[1];
            productIdToProductPriceMap.put(productId, productPrice);
        }

        model.addAttribute("order", order);
        model.addAttribute("statuses",orderStatusRepository.findAll());
        model.addAttribute("user", user);
        model.addAttribute("orderProducts", orderProductList);
        model.addAttribute("productIdToProductPriceMap", productIdToProductPriceMap);

        return "orders_single";
    }

    private void validateIfAllProductsAreInStock(List<OrderProduct> orderProducts) {
        for (OrderProduct orderedProduct : orderProducts) {

            Product product = orderedProduct.getProduct();
            int quantity = orderedProduct.getQuantity();

            if (!isProductQuantityInStockEnough(product, quantity)) {
                throw new NotEnoughStockException(product, getStockAmountOfProduct(product));
            }
        }
    }

    private boolean isProductQuantityInStockEnough(Product product, int quantityRequired) {
        int quantityInStock = getStockAmountOfProduct(product);
        return quantityInStock > quantityRequired;
    }

    private int getStockAmountOfProduct(Product product) {
        validateProductIsInDB(product.getId());

        // Will exist at this point.
        return productRepository.findByIdNotDeleted(product.getId()).get().getQuantity();
    }


    private void validateProductIsInDB(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdNotDeleted(id);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with ID: " + id + " not found in the DB.");
        }
    }
}