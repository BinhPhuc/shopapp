package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.services.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order_details")
@Validated
@RequiredArgsConstructor

public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail (@Valid @RequestBody OrderDetailDTO orderDetailDTO)
            throws NotFoundException {
        OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    @GetMapping("/{order_detail_id}")
    public ResponseEntity<?> getOrderDetail (@Valid @PathVariable("order_detail_id") Long orderDetailId)
            throws NotFoundException {
        OrderDetail existingOrderDetail = orderDetailService.getOrderDetailById(orderDetailId);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(existingOrderDetail));
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetails (@Valid @PathVariable("order_id") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getAllOrders(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(orderDetail -> OrderDetailResponse.fromOrderDetail(orderDetail)).toList();
        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{order_detail_id}")
    public ResponseEntity<?> updateOrderDetail (@Valid @PathVariable("order_detail_id") Long orderDetailId,
                                                @Valid @RequestBody OrderDetailDTO orderDetailDTO) throws NotFoundException {
        OrderDetail newOrderDetail = orderDetailService.updateOrderDetail(orderDetailId, orderDetailDTO);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
    }
    @DeleteMapping("/{order_detail_id}")
    public ResponseEntity<?> deleteOrderDetail (@Valid @PathVariable("order_detail_id") Long orderDetailId) {
        orderDetailService.deleteOrderDetailById(orderDetailId);
        return ResponseEntity.status(HttpStatus.OK).body("delete order detail with id = " + orderDetailId);
    }
}
