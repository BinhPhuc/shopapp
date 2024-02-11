package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDTO orderDTO) throws NotFoundException {
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> {
            return new NotFoundException("Cannot find user with id = " + orderDTO.getUserId());
        });
//        Order newOrder = Order
//                .builder()
//                .fullName(orderDTO.getFullName())
//                .email(orderDTO.getEmail())
//                .phoneNumber(orderDTO.getPhoneNumber())
//                .address(orderDTO.getAddress())
//                .note(orderDTO.getNote())
//                .orderDate(LocalDateTime.now())
//                .totalMoney(orderDTO.getTotalMoney())
//                .shippingMethod(orderDTO.getShippingMethod())
//                .shippingAddress(orderDTO.getShippingAddress())
//                .paymentMethod(orderDTO.getPaymentMethod())
//                .build();
//        mod
//        return orderRepository.save(newOrder);
        return null;
    }

    @Override
    public Order getOrderById(Long orderDTO) {
        return null;
    }

    @Override
    public Order updateOrder(Long orderId, OrderDTO orderDTO) {
        return null;
    }

    @Override
    public void deleteOrderById(Long orderDTO) {

    }

    @Override
    public List<Order> getAllOrders() {
        return null;
    }
}
