package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.exception.PasswordMachingException;
import com.project.shopapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@Validated
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> createUser (@Valid @RequestBody UserDTO userDTO) throws PasswordMachingException, NotFoundException {
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            throw new PasswordMachingException("Password and retype password are not matching!");
        }
        userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Register successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UserLoginDTO userLoginDTO) {
        // kiem tra thong tin dang nhap va sinh token
        String token = userService.logIn(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
