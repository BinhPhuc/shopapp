package com.project.shopapp.controllers;

import com.project.shopapp.models.Role;
import com.project.shopapp.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/roles")
@RestController
@RequiredArgsConstructor

public class RoleController {
    private final RoleService roleService;
    @GetMapping("")
    public ResponseEntity<?> getAllRoles ()  {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
