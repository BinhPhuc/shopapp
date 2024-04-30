package com.project.shopapp.controllers;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.*;
import com.project.shopapp.models.User;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.responses.LoginResponse;
import com.project.shopapp.responses.UserResponse;
import com.project.shopapp.services.IUserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
@Validated
@RequiredArgsConstructor

public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity<?> createUser (@Valid @RequestBody UserDTO userDTO) throws
            PasswordMachingException,
            NotFoundException,
            PermissionDenied,
            ExistDataException
            {
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            throw new PasswordMachingException
                    (localizationUtils.getLocalizeMessage(MessageUtils.REGISTER_FAIL));
        }
        User newUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request)
            throws NotFoundException, UsernameNotFoundException, InvalidParamException {
        // kiem tra thong tin dang nhap va sinh token
        String token = userService.logIn(userLoginDTO);
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message(localizationUtils.getLocalizeMessage(MessageUtils.LOGIN_SUCCESFULLY))
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

    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader) throws ExpiredException,
            NotFoundException {
        String newToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsByToken(newToken);
        return ResponseEntity.ok(UserResponse.builder()
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                .build());
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUserDetail(
            @RequestHeader("Authorization") @NotNull String authorizationHeader,
            @PathVariable("userId") Long userId,
            @RequestBody UserDTO updatedUserDTO) throws ExpiredException, NotFoundException, PermissionDenied,
            PasswordMachingException {
        String newToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsByToken(newToken);
        if(userId != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("cannot update other user profile");
        }
        User updatedUser = userService.updateUser(userId, updatedUserDTO);
        return ResponseEntity.ok(UserResponse.builder()
                .userId(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .phoneNumber(updatedUser.getPhoneNumber())
                .address(updatedUser.getAddress())
                .build());
    }
}
