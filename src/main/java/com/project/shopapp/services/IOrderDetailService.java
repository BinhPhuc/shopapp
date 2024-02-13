package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail (OrderDetailDTO orderDetailDTO) throws NotFoundException;
    OrderDetail getOrderDetailById(Long orderId) throws NotFoundException;
    OrderDetail updateOrderDetail (Long orderDetailId, OrderDetailDTO orderDetailDTO) throws NotFoundException;
    void deleteOrderDetailById(Long orderId);
    List<OrderDetail> getAllOrders (Long orderId);
    
}
