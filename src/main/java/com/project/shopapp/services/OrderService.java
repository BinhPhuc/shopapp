package com.project.shopapp.services;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DateAndTimeException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductService productService;
    @Override
    public Order createOrder(OrderDTO orderDTO)
            throws NotFoundException, DateAndTimeException {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("Cannot find user with id = " + orderDTO.getUserId()));
        Order newOrder = Order
                .builder()
                .fullName(orderDTO.getFullName())
                .email(orderDTO.getEmail())
                .phoneNumber(orderDTO.getPhoneNumber())
                .address(orderDTO.getAddress())
                .note(orderDTO.getNote())
                .status(OrderStatus.pending)
                .orderDate(new Date())
                .totalMoney(orderDTO.getTotalMoney())
                .shippingMethod(orderDTO.getShippingMethod())
                .shippingAddress(orderDTO.getShippingAddress())
                .paymentMethod(orderDTO.getPaymentMethod())
                .active(true)
                .build();
        newOrder.setUser(user);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())) {
            throw new DateAndTimeException("Date must be at least today!");
        }
        newOrder.setShippingDate(shippingDate);
        orderRepository.save(newOrder);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            Long productId = cartItemDTO.getProductId();
            Product currentProduct = productService.getProductById(productId);
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(newOrder)
                    .product(currentProduct)
                    .quantity(cartItemDTO.getQuantity())
                    .price(currentProduct.getPrice())
                    .build();
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return newOrder;
    }

    @Override
    public Order getOrderById(Long orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Cannot find order with id = " + orderId));
        return order;
    }

    @Override
    public List<Order> getAllOrdersById(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrder(Long orderId, OrderDTO orderDTO)
            throws NotFoundException {
        Order existingOrder = getOrderById(orderId);
        User existingUser = userRepository.findById(existingOrder.getUser().getId())
                .orElseThrow(() -> new NotFoundException("Cannot find user with id = " + existingOrder.getUser().getId()));
        existingOrder.setFullName(orderDTO.getFullName());
        existingOrder.setEmail(orderDTO.getEmail());
        existingOrder.setPhoneNumber(orderDTO.getPhoneNumber());
        existingOrder.setAddress(orderDTO.getAddress());
        existingOrder.setNote(orderDTO.getNote());
        existingOrder.setTotalMoney(orderDTO.getTotalMoney());
        existingOrder.setShippingMethod(orderDTO.getShippingMethod());
        existingOrder.setShippingAddress(orderDTO.getShippingAddress());
        existingOrder.setShippingDate(orderDTO.getShippingDate());
        existingOrder.setPaymentMethod(orderDTO.getPaymentMethod());
        existingOrder.setUser(existingUser);
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrderById(Long orderId) throws NotFoundException {
        Order existingOrder = getOrderById(orderId);
        User existingUser = userRepository.findById(existingOrder.getUser().getId())
                .orElseThrow(() -> new NotFoundException("Cannot find user with id = " + existingOrder.getUser().getId()));
        existingOrder.setActive(false);
        orderRepository.save(existingOrder);
    }


}
