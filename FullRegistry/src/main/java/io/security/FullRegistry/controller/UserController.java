package io.security.FullRegistry.controller;

import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.dto.RoleToUserRequest;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/role/new")
    public ResponseEntity<Role> saveRole(@RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveRole(request));
    }

    @PostMapping(value = "/role/add-to-user")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserRequest request) {
        userService.addRoleToUser(request.getEmail(), request.getRoleName());
        return ResponseEntity.ok().build();
    }

}
