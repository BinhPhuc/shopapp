package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    Order createOrder (OrderDTO orderDTO) throws NotFoundException;
    Order getOrderById (Long orderDTO);
    Order updateOrder (Long orderId, OrderDTO orderDTO);
    void deleteOrderById(Long orderDTO);

    List<Order> getAllOrders();
}
