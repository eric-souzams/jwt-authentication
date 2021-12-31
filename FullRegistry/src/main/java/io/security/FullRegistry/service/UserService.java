package io.security.FullRegistry.service;

import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.model.User;

public interface UserService {

    User signUpUser(User user);

    Integer enableAppUser(String email);

    Role saveRole(RoleRequest role);

    void addRoleToUser(String email, String roleName);

}
