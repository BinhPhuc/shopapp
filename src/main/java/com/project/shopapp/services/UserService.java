package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    @Override
    public User createUser(UserDTO userDTO) throws NotFoundException {
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number alreadt exists!");
        };
//        User newUser = User
//                .builder()
//                .fullName(userDTO.getFullName())
//                .phoneNumber(userDTO.getPhoneNumber())
//                .address(userDTO.getAddress())
//                .password(userDTO.getPassword())
//                .dateOfBirth(userDTO.getDateOfBirth())
//                .facebookAccountId(userDTO.getFacebookAccountId())
//                .googleAccountId(userDTO.getGoogleAccountId())
//                .build();
        Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new NotFoundException("Role not found"));
        User newUser = modelMapper.map(userDTO, User.class);
        newUser.setRole(role);
        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            newUser.setPassword(password);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String logIn(String phoneNumber, String password) {
        return null;
    }
}
