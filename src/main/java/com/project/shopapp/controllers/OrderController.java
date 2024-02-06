package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.dtos.UserDTO;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
@Validated

public class OrderController {
    @PostMapping("")
    public ResponseEntity<?> createOrder (@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.status(HttpStatus.OK).body("create order sucessfully!");
    }
    @GetMapping("/{user_id}")
    public ResponseEntity<?> getOrder (@Valid @PathVariable("user_id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(String.format("Get user's %d order list!", userId));
    }
    @PutMapping("/{user_id}")
    public ResponseEntity<?> updateOrder (@Valid @PathVariable("user_id") Long userID,
                                          @Valid @RequestBody OrderDTO orderDTO ) {
        return ResponseEntity.status(HttpStatus.OK).body("Update order succesfully!");
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteOrder (@Valid @PathVariable("user_id") Long userID) {
        // active -> 0
        return ResponseEntity.status(HttpStatus.OK).body("Delete order succesfully!");
    }
}
