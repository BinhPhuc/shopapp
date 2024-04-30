package com.project.shopapp.services;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exception.*;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;
    @Override
    public User createUser(UserDTO userDTO) throws NotFoundException, PermissionDenied, ExistDataException {
        // Register user
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ExistDataException(
                    localizationUtils.getLocalizeMessage(MessageUtils.PHONE_EXIST)
            );
        };
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new NotFoundException(
                        localizationUtils.getLocalizeMessage(MessageUtils.ROLE_NOT_FOUND))
                );
        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenied(
                    localizationUtils.getLocalizeMessage(MessageUtils.PERMISSION_DENIED)
                );
        }
        User newUser = User
                .builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .password(userDTO.getPassword())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);
        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String logIn(UserLoginDTO userLoginDTO) throws NotFoundException, UsernameNotFoundException, InvalidParamException {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
        if(optionalUser.isEmpty()) {
            throw new NotFoundException(
                    localizationUtils.getLocalizeMessage(MessageUtils.LOGIN_FAIL)
            );
        }
//        if(userLoginDTO.getRoleId() == 2) {
//            throw new PermissionDenied()
//        }
        User existingUser = optionalUser.get();
        if(existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizeMessage(MessageUtils.LOGIN_FAIL)
                );
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }

    public User getUserById(Long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MessageUtils.USER_NOT_FOUND));
    }

    @Override
    public User getUserDetailsByToken(String token) throws ExpiredException, NotFoundException {
        if(jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredException("token is expired!");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isEmpty()) {
            throw new NotFoundException("user not found!");
        } else {
            return user.get();
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDTO updateUserDTO) throws NotFoundException, PermissionDenied,
            PasswordMachingException {
        Optional<User> optionalUser = userRepository.findById(userId);
        User existingUser = optionalUser.get();
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("Cannot find user with id = " + userId);
        }
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new PermissionDenied("Phone number already exists");
        }
        if(updateUserDTO.getFullName() != null) {
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if(updateUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if(updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if(updateUserDTO.getPassword() != null &&
                updateUserDTO.getRetypePassword() != null) {
            if(updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                throw new PasswordMachingException("Password and retype password are not matching");
            }
            String password = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(encodedPassword);
        }
        if(updateUserDTO.getGoogleAccountId() != null && updateUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }
        if(updateUserDTO.getFacebookAccountId() != null && updateUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        return userRepository.save(existingUser);
    }

}
