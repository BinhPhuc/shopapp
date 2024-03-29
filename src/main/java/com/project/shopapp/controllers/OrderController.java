package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DateAndTimeException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderListResponse;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@Validated
@RequiredArgsConstructor

public class OrderController {
    private final IOrderService orderService;
    @PostMapping("")
    public ResponseEntity<?> createOrder (@Valid @RequestBody OrderDTO orderDTO)
            throws NotFoundException, DateAndTimeException {
        Order order = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(OrderResponse.fromOrder(order));
    }
    @GetMapping("/{order_id}")
    public ResponseEntity<?> getOrder (@Valid @PathVariable("order_id") Long orderId)
            throws NotFoundException {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders (@Valid @PathVariable("user_id") Long userId)
            throws NotFoundException {
        List<Order> orderList = orderService.getAllOrdersById(userId);
        return ResponseEntity.ok(orderList);
    }
    @PutMapping("/{order_id}")
    @Transactional
    public ResponseEntity<?> updateOrder (@Valid @PathVariable("order_id") Long orderId,
                                          @Valid @RequestBody OrderDTO orderDTO ) throws NotFoundException {
        Order newOrder = orderService.updateOrder(orderId, orderDTO);
        return ResponseEntity.ok(newOrder);
    }

    @DeleteMapping("/{order_id}")
    @Transactional
    public ResponseEntity<?> deleteOrder (@Valid @PathVariable("order_id") Long orderId)
            throws NotFoundException {
        // active -> 0
        orderService.deleteOrderById(orderId);
        return ResponseEntity.ok("Delete order with id = " + orderId + " successfully!");
    }
}
