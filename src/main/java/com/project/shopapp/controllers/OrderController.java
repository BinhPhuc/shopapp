package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DateAndTimeException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.responses.OrderListResponse;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.IOrderDetailService;
import com.project.shopapp.services.IOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@Validated
@RequiredArgsConstructor

public class OrderController {
    @Autowired
    private final IOrderService orderService;
    @Autowired
    private final IOrderDetailService orderDetailService;
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
        List<OrderDetail> orderDetails = orderDetailService.getAllOrders(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(orderDetail -> OrderDetailResponse.fromOrderDetail(orderDetail)).toList();
        return ResponseEntity.ok(OrderResponse.builder()
                        .id(order.getId())
                        .userId(order.getUser().getId())
                        .fullName(order.getFullName())
                        .email(order.getEmail())
                        .phoneNumber(order.getPhoneNumber())
                        .address(order.getAddress())
                        .note(order.getNote())
                        .orderDate(order.getOrderDate())
                        .totalMoney(order.getTotalMoney())
                        .shippingMethod(order.getShippingMethod())
                        .shippingAddress(order.getShippingAddress())
                        .paymentMethod(order.getPaymentMethod())
                        .orderDetails(orderDetailResponses)
                        .build());
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

    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getOrdersByKeyword(@RequestParam(defaultValue = "0", name = "page") int page,
                                                @RequestParam(defaultValue = "11", name = "limit") int limit,
                                                @RequestParam(name = "keyword") String keyword) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<OrderResponse> orderPage = orderService.getOrderByKeyword(keyword, pageable);
        List<OrderResponse> orderResponseList = orderPage.getContent();
        int totalPages = orderPage.getTotalPages();
        return ResponseEntity.ok(OrderListResponse
                .builder()
                        .orderResponseList(orderResponseList)
                        .totalPages(totalPages)
                .build());
    }
}
