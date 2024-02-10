package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.User;

import java.util.List;

public interface IUserService {
    User createUser(UserDTO userDTO) throws NotFoundException;

    String logIn(String phoneNumber, String password);
}
