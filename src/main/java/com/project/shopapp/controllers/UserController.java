package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.exception.PermissionDenied;
import com.project.shopapp.models.User;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.services.IUserService;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.exception.PasswordMachingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@Validated
@RequiredArgsConstructor

public class UserController {
    private final IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> createUser (@Valid @RequestBody UserDTO userDTO) throws
            PasswordMachingException,
            NotFoundException,
            PermissionDenied
            {
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            throw new PasswordMachingException("Password and retype password are not matching!");
        }
        userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Register successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UserLoginDTO userLoginDTO)
            throws NotFoundException, UsernameNotFoundException, InvalidParamException {
        // kiem tra thong tin dang nhap va sinh token
        String token = userService.logIn(userLoginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUser (@PathVariable("user_id") Long userId)
            throws NotFoundException {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
