package com.project.shopapp.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.*;
import com.project.shopapp.models.User;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.responses.LoginResponse;
import com.project.shopapp.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@Validated
@RequiredArgsConstructor

public class UserController {
    private final IUserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    @PostMapping("/register")
    public ResponseEntity<?> createUser (@Valid @RequestBody UserDTO userDTO) throws
            PasswordMachingException,
            NotFoundException,
            PermissionDenied,
            ExistDataException
            {
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            throw new PasswordMachingException("Password and retype password are not matching!");
        }
        User newUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request)
            throws NotFoundException, UsernameNotFoundException, InvalidParamException {
        // kiem tra thong tin dang nhap va sinh token
        String token = userService.logIn(userLoginDTO);
        Locale locale = localeResolver.resolveLocale(request);
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message(messageSource.getMessage("user.login.login_successfully", null, locale))
                        .token(token)
                        .build()
        );
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUser (@PathVariable("user_id") Long userId)
            throws NotFoundException {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
