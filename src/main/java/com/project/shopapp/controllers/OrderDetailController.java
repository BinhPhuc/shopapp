package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order_details")
@Validated

public class OrderDetailController {
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail (@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.status(HttpStatus.OK).body("createOrderDetail here!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail (@Valid @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body("get order detail with id = " + id);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails (@Valid @PathVariable("orderId") Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body("getOrderDetails with orderId = " + orderId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail (@Valid @PathVariable("id") Long id,
                                                @Valid @RequestBody OrderDetailDTO newOrderDetailData) {
        return ResponseEntity.status(HttpStatus.OK).body("update order detail with id = " + id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail (@Valid @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body("delete order detail with id = " + id);
    }
}
