package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DateAndTimeException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderListResponse;
import com.project.shopapp.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder (OrderDTO orderDTO) throws NotFoundException, DateAndTimeException;
    Order getOrderById(Long orderDTO) throws NotFoundException;
    List<Order> getAllOrdersById(Long userId);
    Order updateOrder (Long orderId, OrderDTO orderDTO) throws NotFoundException;
    void deleteOrderById(Long orderDTO) throws NotFoundException;

    Page<OrderResponse> getOrderByKeyword(String keyword, Pageable pageable);
}
