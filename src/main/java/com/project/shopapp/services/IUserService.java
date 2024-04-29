package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exception.*;
import com.project.shopapp.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface IUserService {
    User createUser(UserDTO userDTO) throws NotFoundException, PermissionDenied, ExistDataException;

    String logIn(UserLoginDTO userLoginDTO) throws
            NotFoundException,
            UsernameNotFoundException,
            InvalidParamException;

    User getUserById(Long userId) throws NotFoundException;

    User getUserDetailsByToken(String token) throws ExpiredException, NotFoundException;
}
